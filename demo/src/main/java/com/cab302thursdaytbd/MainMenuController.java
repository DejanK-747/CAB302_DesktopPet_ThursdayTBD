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

    private int userId;
    private PetService petService = new PetService(userId);
    private PetDAO petDAO = new PetDAO();

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void switchToMain() {
        try {
            App.setRoot("main_page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void switchToAbout() {
        try {
            App.setRoot("about");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }

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
