package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.Pet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// this is the controller for the pet death screen
// the screen is show when the pet dies (duh) due to starving or being too exhausted
// it displays the dfinal pet information, cause of death, pets farewell message, and shows the pets image
// it also allows the user to adopt  a new pet or quit the application


public class PetDeathController {
    // fxml bindings linked to elements inside of pet_death.fxml
    @FXML private ImageView tombstoneView;
    @FXML private Label levelLabel;
    @FXML private Label quoteLabel;
    @FXML private Label petNameLabel;
    @FXML private Label reasonLabel;
    private int userId;


    // farewell quotes to randomly display when pet dies. stored in a static array so all instances will share the same list.
    private static final String[] QUOTES = {
            "\"...I knocked over your water glass every morning just to see you. I would have done it forever.\"",
            "\"I may have been small, but I loved you in every way I knew how.\"",
            "\"You gave me a name, and that meant everything.\"",
            "\"I waited for you every day. I never stopped.\""
    };

    // death screen initialisation. called when the pet dies. it receives the final Pet object, and the reason for death
    public void initDeathScreen(Pet pet, String reason) {
        if (pet != null) {
            this.userId = pet.getUserId(); // store user id
            petNameLabel.setText(pet.getPetName()); // display pet name
            levelLabel.setText("Lvl. " + Math.max(1, pet.getId())); //temp level display

            // picks a random farewell quote using the math method.
            // math.random returns a decimal between 0.0 and 1.0, multiplying it by QUOTES.length scales the value to the
            // size of the quotes array
            // casting to int removes the decimal portion, making a valid array index
            int index = (int)(Math.random() * QUOTES.length);
            // displays the random quote
            quoteLabel.setText(QUOTES[index]);

            // displays the death reason
            reasonLabel.setText("Reason: " + reason);

            // loads the correct image based on the pets type stored in the db
            try {
                String imgPath;
                switch (pet.getPetType()) {
                    case "cat":
                        imgPath = "/com/cab302thursdaytbd/images/Cat-happy.png";
                        break;
                    case "monkey":
                        imgPath = "/com/cab302thursdaytbd/images/Monkey1.png";
                        break;
                    default: //fallback image in case of error/unknown pet
                        imgPath = "/com/cab302thursdaytbd/images/frog1.png";
                        break;
                }
                //creates a javafx image object using the selected image path adn displays it in the image view
                tombstoneView.setImage(new Image(getClass().getResource(imgPath).toExternalForm()));
            } catch (Exception ignored) {} // prevents program from crashing if the img cant be loaded
        }
    }

    @FXML
    private void handleQuit() {
        Platform.exit();
    }// this just exits the program

    @FXML
    private void handleAdoptNew() { // returns user to pet selection screen to adopt a new pet.

        try {
            App.setRoot("pet_selection1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

