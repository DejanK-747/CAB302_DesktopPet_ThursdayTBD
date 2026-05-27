package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Model.Session;
import com.cab302thursdaytbd.Model.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private PetDAO petDao = new PetDAO();
    private PetSelectionController petSelectionController = new PetSelectionController();

    // Runs when "Login" Button is pressed
    @FXML
    private void handleLogin() throws IOException {
        // Get text entered by the user and remove extra spaces
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Prevents login if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }

        // Login using UserDAO
        int userId = UserDAO.loginUser(username, password);

        // Successful login
        if (userId > 0) {
            // Stores logged-in user in session
            Session.setUser(userId, username);
            statusLabel.setText("Login successful!");
            // Goes to next page
            goToApp();
        } else {
            // Failed login
            statusLabel.setText("Invalid username or password");
        }
    }

    // Opens register.fxml when button is clicked
    @FXML
    private void goToRegister() {
        try {
            App.setRoot("register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
