package com.cab302thursdaytbd;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Fields cannot be empty");
            return;
        }

        boolean success = UserDAO.registerUser(username, password);

        if (success) {
            statusLabel.setText("Registered successfully!");
        } else {
            statusLabel.setText("Registration failed");
        }
    }
}
