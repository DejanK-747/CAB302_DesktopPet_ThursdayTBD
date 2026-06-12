package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

/**
 * Controller for the login page.
 * Handles user login, navigation to the registration page,
 * and redirects authenticated users to either pet selection
 * or the main application page depending on whether they
 * already have an adopted pet.
 */
public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private PetDAO petDao = new PetDAO();
    private IUserDAO userDAO = new UserDAO();

    /**
     * Handles the login button action.
     * Validates that the username and password fields are not empty,
     * authenticates the user, stores the user in the Session Class,
     * and navigates to the appropriate page if login is successful.
     * @throws IOException If the application cannot load the next FXML page.
     */
    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }

        int userId = userDAO.loginUser(username, password);

        if (userId > 0) {
            Session.setUser(userId, username);
            statusLabel.setText("Login successful!");
            goToApp();
        } else {
            statusLabel.setText("Invalid username or password");
        }
    }

    /**
     * Navigates from the login page to the registration page.
     */
    @FXML
    private void goToRegister() {
        try {
            App.setRoot("register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates the authenticated user to the correct application page.
     * If the logged-in user does not already have a pet, the pet
     * selection page is shown. Otherwise, the user is taken directly to the main page.
     * @throws IOException If the application cannot load the required FXML page.
     */
    @FXML private void goToApp() throws IOException {
        try {
            int sessionUser = Session.getUserId();

            Pet userPet = petDao.getPet(sessionUser);

            if (userPet == null){
                App.setRoot("pet_selection1");

            } else {
                App.setRoot("main_page");
            }

        } catch(NullPointerException e) {
            System.out.println("User session is not set");
        }
    }
}
