package com.desdulianto.learning.imvertx.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class LoginDialog extends Dialog<Pair<String, String>> {
    public LoginDialog() {
        this.setTitle("Vertx IM Login");
        this.setHeaderText("Login");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField username = new TextField();
        username.setPromptText("User");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("User"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password"), 0, 1);
        grid.add(password, 1, 1);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        Node loginButton = this.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        username.textProperty().addListener((observable, OldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        this.getDialogPane().setContent(grid);

        Platform.runLater(username::requestFocus);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
    }
}
