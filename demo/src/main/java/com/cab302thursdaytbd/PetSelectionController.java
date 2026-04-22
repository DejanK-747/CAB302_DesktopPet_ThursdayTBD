package com.cab302thursdaytbd;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class PetSelectionController {

    private PetDAO petDAO = new PetDAO();

    @FXML
    private TextField petNameField;

    @FXML
    private Button adoptButton;

    @FXML
    private ImageView petView;

    private int currentFrame = 0;
    private Image[] frames;

    private int currentPetIndex = 0;
    private final String[] petType = {"frog", "cat"};

    private int userId;


    @FXML
    public void initialize() {
        frames = new Image[] {
                new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog1.png").toExternalForm()),
                new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog2.png").toExternalForm())
        };

        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300), e -> {
                    currentFrame = (currentFrame + 1) % frames.length;
                    petView.setImage(frames[currentFrame]);
                })
        );

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        adoptButton.disableProperty().bind(
          javafx.beans.binding.Bindings.createBooleanBinding(
                  () -> petNameField.getText().trim().isEmpty(),
                  petNameField.textProperty()
          )
        );

    }

    @FXML
    private void onLeftArrowClick() {
        switchPet(-1);
    }

    @FXML
    private void onRightArrowClick() {
        switchPet(1);
    }



    private void switchPet(int direction) {

        currentPetIndex = (currentPetIndex + direction + petType.length) % petType.length;
        String newPet = petType[currentPetIndex];

        javafx.animation.TranslateTransition slideOut =
                new javafx.animation.TranslateTransition(Duration.millis(300), petView);
        slideOut.setByX(direction * -300);

        slideOut.setOnFinished( e -> {
                startAnimationFor(newPet);

                petView.setTranslateX(direction * 300);
                javafx.animation.TranslateTransition slideIn =
                        new javafx.animation.TranslateTransition(Duration.millis(300), petView);
                slideIn.setToX(0);
                slideIn.play();
        });
        slideOut.play();
    }

    private void startAnimationFor(String pet) {
        if (pet.equals("frog")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog2.png").toExternalForm())
            };
        }

        if (pet.equals("cat")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Cat-happy.png").toExternalForm())
            };
        }
        currentFrame = 0;
        petView.setImage(frames[0]);


    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    @FXML
    private void handleAdopt() {
        System.out.println("ADOPT BUTTON CLICKED");

        String petName = petNameField.getText().trim();

        if (petName.isEmpty()) {
            System.out.println("Invalid name");
            return;
        }

        String selectedPet = petType[currentPetIndex];

        petDAO.adoptPet(userId, selectedPet, petName);

        System.out.println("Pet: " + selectedPet + ", Name: " + petName);

    }


}

