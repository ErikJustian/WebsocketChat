package com.desdulianto.learning.imvertx.client;

import com.desdulianto.learning.imvertx.model.User;
import com.desdulianto.learning.imvertx.packet.ChatMessage;
import com.desdulianto.learning.imvertx.packet.ConversationMessage;
import com.desdulianto.learning.imvertx.packet.ListOnlineUsersMessage;
import com.desdulianto.learning.imvertx.packet.LoginMessage;
import com.desdulianto.learning.imvertx.packet.LoginNotification;
import com.desdulianto.learning.imvertx.packet.LogoutNotification;
import com.desdulianto.learning.imvertx.packet.OnlineUsers;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IMGui extends Application implements Initializable {
    @FXML
    private TextArea areaChat;
    @FXML
    private TextField txtChat;
    @FXML
    private Button btnSend;
    @FXML
    private ListView<String> lstUsers;

    private IMClientVerticle verticle;

    private static Vertx vertx;

    private User user;

    private Logger logger;

    private Map<String, List<ConversationMessage>> chats;

    public IMGui() {
        logger = Logger.getLogger(getClass().getName());
        chats = new HashMap<>();
    }


    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ChatWindow.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Vertx Instant Messenger");
        stage.show();
        stage.setOnCloseRequest(event -> {
            if (vertx != null) vertx.close();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startVerticle();

        lstUsers.setOnMouseClicked(value -> {
            String username = lstUsers.getSelectionModel().getSelectedItem();
            if (username != null) {
                List<ConversationMessage> chat = getChat(username);

                areaChat.clear();
                areaChat.textProperty().bindBidirectional(new SimpleStringProperty(chat.stream()
                        .map(x -> x.getFrom() + ": " + x.getMessage()).collect(Collectors.joining("\n"))));
            }
        });
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void startVerticle() {
        LoginDialog login = new LoginDialog();
        login.initModality(Modality.APPLICATION_MODAL);
        Pair<String, String> loginData = login.showAndWait().orElse(new Pair<>("", ""));

        IMGui.vertx = Vertx.vertx();
        verticle = new IMClientVerticle();
        vertx.deployVerticle(verticle, stringAsyncResult -> {
            if (stringAsyncResult.succeeded()) {
                logger.info("Success starting verticle");

                // consume incoming message
                verticle.incomingMessageHandler(this::handleIncomingMessage);

                // send outgoing message
                btnSend.setOnAction(this::handleSendMessage);
                verticle.outgoingMessageHandler(JsonObject.mapFrom(new LoginMessage(loginData.getKey(), loginData.getValue())));
                this.user = new User(loginData.getKey());
            } else {
                logger.warning("Failed starting verticle");
                logger.severe(Arrays.toString(stringAsyncResult.cause().getStackTrace()));
            }

        });
    }

    private void handleIncomingMessage(JsonObject message) {
        ChatMessage m = message.mapTo(ChatMessage.class);
        if (m instanceof ConversationMessage) {
            ConversationMessage o = (ConversationMessage) m;
            //areaChat.appendText(o.getFrom() + ": " + o.getMessage() + "\n");
            getChat(o.getFrom()).add(o);
        } else if (m instanceof LoginNotification || m instanceof LogoutNotification) {
            verticle.outgoingMessageHandler(JsonObject.mapFrom(new ListOnlineUsersMessage()));
        } else if (m instanceof OnlineUsers) {
            Platform.runLater(() -> {
                lstUsers.getItems().clear();
                lstUsers.getItems().addAll(((OnlineUsers) m).getUsers().stream().map(User::getUsername)
                        .collect(Collectors.toSet()));
            });
        }
    }

    private void handleSendMessage(ActionEvent event) {
        String username = lstUsers.getSelectionModel().getSelectedItem();
        if (username != null) {
            List<ConversationMessage> chat = getChat(username);
            ConversationMessage message = new ConversationMessage(txtChat.getText(), this.user.getUsername(), username);
            chat.add(message);
            verticle.outgoingMessageHandler(JsonObject.mapFrom(message));
            txtChat.clear();
            txtChat.requestFocus();
        }
    }

    private List<ConversationMessage> getChat(String username) {
        List<ConversationMessage> chat = chats.get(username);
        if (chat == null) {
            chats.put(username, new ArrayList<>());
            chat = chats.get(username);
        }
        return chat;
    }
}
