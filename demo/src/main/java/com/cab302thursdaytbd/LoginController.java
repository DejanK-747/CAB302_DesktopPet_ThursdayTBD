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

    @FXML
    private void handleLogin() throws IOException {

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
            goToApp();
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
