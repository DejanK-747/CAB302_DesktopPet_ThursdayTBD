package com.cab302thursdaytbd;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PetSelectionController {

    @FXML
    private ImageView petView;

    private int currentFrame = 0;
    private Image[] frames;

    private int currentPetIndex = 0;
    private final String[] pets = {"frog", "cat"};

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

        currentPetIndex = (currentPetIndex + direction + pets.length) % pets.length;
        String newPet = pets[currentPetIndex];

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
}

