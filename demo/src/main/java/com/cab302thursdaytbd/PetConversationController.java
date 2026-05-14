package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Model.Session;
import com.cab302thursdaytbd.Service.PetConversationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class PetConversationController {
    private final PetDAO petDAO = new PetDAO();
    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField messageField;

    private PetConversationService petService;
    int userId;
    @FXML
    public void initialize() {
        userId = Session.getUserId();
        petService = new PetConversationService();
    }
    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main_page.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(newRoot);
        stage.show();
        stage.setResizable(false);

    }
    @FXML
    private void handleSendButtonAction() {
        Pet pet = petDAO.getPet(userId);

        String prompt = messageField.getText();
        String aiPrompt = "In this the energy level, hunger 10 being fully fed, affection and boredom have a scale from 1 - 10. You should reply as if you are a " + pet.getPetType() + " with the name " + pet.getPetName() + " do not reply with actions, that current has a energy level of " + pet.getEnergy() + " a boredom scale of " + pet.getBoredom() + " a hunger level of " + pet.getHunger() + " and a affection level of " + pet.getAffection() +" prompt";
        chatTextArea.appendText("*"+Session.getUsername() + "*\n" + prompt + "\n\n");
        String response;
        try {
            response = petService.askQuestion(aiPrompt);
        } catch (Exception e) {
            response = e.getMessage();
        }
        chatTextArea.appendText("*"+pet.getPetName()+"*\n" + response + "\n\n");
    }
}

