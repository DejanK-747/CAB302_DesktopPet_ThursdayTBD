package com.cab302thursdaytbd;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void goToLogin() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Fields cannot be empty");
            return;
        }
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            return;
        }

        int userId = UserDAO.registerUser(username, password);

        if (userId > 0) {
            Session.setUser(userId, username);
            statusLabel.setText("Registered successfully!");
        } else {
            statusLabel.setText("Registration failed");
        }
    }
}
