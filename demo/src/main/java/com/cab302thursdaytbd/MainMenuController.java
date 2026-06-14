package com.cab302thursdaytbd;

import java.io.IOException;

import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Model.Session;
import com.cab302thursdaytbd.Service.PetService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;



public class MainMenuController {

    /// Sets User ID
    private int userId;
    private PetService petService = new PetService(userId);
    private PetDAO petDAO = new PetDAO();

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /// Allows navigation to main page
    @FXML
    private void switchToMain() {
        try {
            App.setRoot("main_page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Allows navigation to stats page while using the User ID
    @FXML
    private void switchToStats() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("stats.fxml"));
            Parent root = loader.load();
            PetStatsController statsController = loader.getController();
            statsController.setUserId(Session.getUserId());
            App.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Allows Navigation to about page
    @FXML
    private void switchToAbout() {
        try {
            App.setRoot("about");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Takes user back to login page allowing them to change account
    @FXML
    private void switchToLogin() throws IOException {
        Session.clear();
        App.setRoot("login");
    }

    /// Kills the pet associated with the User ID
    @FXML
    private void killPetButton() throws IOException{
        try {
            petService.killPet(userId);

            Pet deadPet = petDAO.getPet(userId);

            String reason = petService.determineDeathReason(deadPet);


            FXMLLoader loader = new FXMLLoader(App.class.getResource("pet_death.fxml"));
            Parent root = loader.load();
            PetDeathController deathController = loader.getController();
            deathController.initDeathScreen(deadPet, reason);
            App.getScene().setRoot(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
