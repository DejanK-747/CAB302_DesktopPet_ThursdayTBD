package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.IUserDAO;
import com.cab302thursdaytbd.Model.Session;
import com.cab302thursdaytbd.Model.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the user registration page.
 * Handles account creation, input validation,
 * and navigation back to the login page.
 */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;
    private IUserDAO userDAO = new UserDAO();
    /**
     * Navigates the user back to the login page.
     */
    @FXML
    private void goToLogin() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the account registration process.
     * Validates user input, ensures that the password
     * confirmation matches, creates a new user account
     * through the UserDAO, and stores the newly created
     * user in the current session.
     */
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
        int userId = userDAO.registerUser(username, password);

        if (userId > 0) {
            Session.setUser(userId, username);
            statusLabel.setText("Registered successfully!");
        } else {
            statusLabel.setText("Registration failed");
        }
    }
}
