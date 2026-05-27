package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.Session;
import com.cab302thursdaytbd.Model.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    // Handles "BackButton" and takes user to login page
    @FXML
    private void goToLogin() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Runs when "Create Account" is pressed
    @FXML
    private void handleRegister() {
        // Text from user and removes extra spaces
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Prevents registration if any fields are empty
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Fields cannot be empty");
            return;
        }
        // Check if the password and confirmation password match
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            return;
        }
        // Creates the new user in the database and returns userID
        int userId = UserDAO.registerUser(username, password);

        // Successful registration returns userID > 0
        if (userId > 0) {
            Session.setUser(userId, username);
            statusLabel.setText("Registered successfully!");
        } else {
            // Registration failed
            statusLabel.setText("Registration failed");
        }
    }
}
