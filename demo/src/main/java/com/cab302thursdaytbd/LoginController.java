package com.cab302thursdaytbd;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }

        int userId = UserDAO.loginUser(username, password);

        if (userId > 0) {
            Session.setUser(userId, username);
            statusLabel.setText("Login successful!");
            // switch to main screen.
        } else {
            statusLabel.setText("Invalid username or password");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            App.setRoot("register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
