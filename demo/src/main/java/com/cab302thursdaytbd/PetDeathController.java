package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.Pet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PetDeathController {

    @FXML private ImageView tombstoneView;
    @FXML private Label levelLabel;
    @FXML private Label quoteLabel;
    @FXML private Label petNameLabel;
    @FXML private Label reasonLabel;
    private int userId;

    private static final String[] QUOTES = {
            "\"...I knocked over your water glass every morning just to see you. I would have done it forever.\"",
            "\"I may have been small, but I loved you in every way I knew how.\"",
            "\"You gave me a name, and that meant everything.\"",
            "\"I waited for you every day. I never stopped.\""
    };

    // Called from PetStatsController when the pet dies
    public void initDeathScreen(Pet pet, String reason) {
        if (pet != null) {
            this.userId = pet.getUserId();
            petNameLabel.setText(pet.getPetName());
            levelLabel.setText("Lvl. " + Math.max(1, pet.getId()));

            // Pick a random farewell quote
            int index = (int)(Math.random() * QUOTES.length);
            quoteLabel.setText(QUOTES[index]);

            // Set reason
            reasonLabel.setText("Reason: " + reason);

            // Set pet image
            try {
                String imgPath;
                switch (pet.getPetType()) {
                    case "cat":
                        imgPath = "/com/cab302thursdaytbd/images/Cat-happy.png";
                        break;
                    case "monkey":
                        imgPath = "/com/cab302thursdaytbd/images/Monkey1.png";
                        break;
                    default:
                        imgPath = "/com/cab302thursdaytbd/images/frog1.png";
                        break;
                }
                tombstoneView.setImage(new Image(getClass().getResource(imgPath).toExternalForm()));
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleQuit() {
        Platform.exit();
    }

    @FXML
    private void handleAdoptNew() {

        try {
            App.setRoot("pet_selection1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

