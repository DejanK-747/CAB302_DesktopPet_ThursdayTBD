package com.cab302thursdaytbd;

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


public class PetConversationController {

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField messageField;

    private PetConversationService petService;

    @FXML
    public void initialize() {
        petService = new PetConversationService();
    }
    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource("pet_selection1.fxml"));

        // 2. Get the current Stage from the button that was clicked
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        // 3. Set the new root to the existing Scene (smoother transition)
        stage.getScene().setRoot(newRoot);
        stage.show();
        stage.setResizable(false);

    }
    @FXML
    private void handleSendButtonAction() {
        String prompt = this.messageField.getText();
        String AIprompt = "You should reply as if you are a pet do not reply with actions, that current has a mood of sad with a curiosity level below average " + prompt;
        chatTextArea.appendText("*USER*\n" + prompt + "\n\n");
        String response;
        try {
            response = petService.askQuestion(AIprompt);
        } catch (Exception e) {
            response = e.getMessage();
        }
        chatTextArea.appendText("*PET NAME*\n" + response + "\n\n");
    }
}

