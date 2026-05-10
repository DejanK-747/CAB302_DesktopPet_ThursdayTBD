package com.cab302thursdaytbd;


import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Model.Session;
import com.cab302thursdaytbd.Service.PetService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;

public class MainPageController {

    private PetDAO petDao = new PetDAO();
    private PetService petService;

    private Pet sessionPet;
    private int sessionUserId;


    @FXML private ProgressBar hungerBar;
    @FXML private ProgressBar affectionBar;
    @FXML private ProgressBar energyBar; //needs to be changed. Visually appears as Cleanliness

    @FXML private Text moodText;
    @FXML private Text petName;
    @FXML private Text levelText;

    @FXML private ImageView petView;
    private int currentFrame;
    private Image[] frames;


    @FXML private Label statusChangeLabel;
    @FXML private Pane speechPane;

    private ParallelTransition statusChangePopUpAnim = new ParallelTransition();

    @FXML
    private Button interactButton1;

    @FXML private Pane pane;

    @FXML
    private ImageView foodItem1;

    @FXML private AnchorPane popUp1;

    private Timeline refreshLoop;
    private Timeline decayLoop;



    @FXML public void initialize() {
        sessionUserId = Session.getUserId();
        sessionPet = petDao.getPet(sessionUserId);
        petService = new PetService(sessionUserId);

        frames = petService.getFrames(sessionPet.getPetType());
        petView.setImage(frames[currentFrame]);

        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300), e -> {
                    currentFrame = (currentFrame + 1) % frames.length;
                    petView.setImage(frames[currentFrame]);
                })
        );

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        loadPet();
        petName.setText(sessionPet.getPetName());

        // TO-DO: initialize map to pet sprite image



        // Duplicate code from Pet Stats. should be moved to PetService later
        petService.startDecay(() -> {
            Platform.runLater(() -> {
                try {
                    Pet deadPet = petDao.getPet(sessionUserId);
                    petService.stop();
                    String reason = "testing";

                    // delete from database immediately
                    petDao.deletePet(sessionUserId);

                    FXMLLoader loader = new FXMLLoader(App.class.getResource("pet_death.fxml"));
                    Parent root = loader.load();
                    PetDeathController deathController = loader.getController();
                    deathController.initDeathScreen(deadPet, reason);
                    App.getScene().setRoot(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        startAutoRefresh();
    }


    //----------------------------------
    // Functions if there are more things that can be done with interactions
    // I was thinking of implementing multiple foods options or different ways to clean the pet
    // Obviously, kind of difficult to implement.
    // Thinking I should limit goals first. Just have these buttons raise stats first.
    @FXML protected void showPopUp1() {
        showPopUp(popUp1);
    }


    protected void showPopUp(AnchorPane popUp) {
        if (popUp.getScaleX() == 0) {
            ScaleTransition transition = new ScaleTransition(Duration.seconds(0.25), popUp);
            transition.setToX(1);
            transition.setToY(1);
            transition.setInterpolator(Interpolator.LINEAR);

            transition.play();

        } else if (popUp.getScaleX() == 1){
            ScaleTransition transition = new ScaleTransition(Duration.seconds(0.25), popUp);
            transition.setToX(0);
            transition.setToY(0);
            transition.setInterpolator(Interpolator.LINEAR);

            transition.play();
        }
    }
    //-----------------------------------------

    protected void statusChangePopUp(String text){

        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {
            statusChangeLabel.setText(text);

            //Animation Handling
            TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.5), statusChangeLabel);
            translateAnimation.setToY(-50);

            statusChangePopUpAnim.getChildren().add(translateAnimation);

            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), statusChangeLabel);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0);
            fadeTransition.setInterpolator(Interpolator.LINEAR);

            statusChangePopUpAnim.getChildren().add(fadeTransition);

            statusChangePopUpAnim.play();

            statusChangeLabel.setTranslateY(0);
        }
    }

    // There should be a better way to handle throttling, but this cheat will do for now
    // Also consider multi threading so decay can still run while still updating stats
    @FXML protected void foodBoost(){
        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {
            int currentHunger = sessionPet.getHunger();
            statusChangePopUp("Hunger");

            sessionPet.setHunger(currentHunger + 1);

            petDao.updatePetStats(sessionPet);
            loadPet();
        }
    }

    @FXML protected void energyBoost() {
        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {
            int currentEnergy = sessionPet.getEnergy();
            statusChangePopUp("Energy");

            sessionPet.setEnergy(currentEnergy + 1);

            petDao.updatePetStats(sessionPet);
            loadPet();
        }
    }

    @FXML protected void affectionBoost() {
        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {

            int currentAffection = sessionPet.getAffection();

            statusChangePopUp("Affection");

            sessionPet.setAffection(currentAffection + 1);

            petDao.updatePetStats(sessionPet);
            loadPet();
        }
    }

    @FXML protected void boredomReduce() {
        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {

            int currentBoredom = sessionPet.getBoredom();

            statusChangePopUp("Boredom");

            sessionPet.setBoredom(currentBoredom - 1);

            petDao.updatePetStats(sessionPet);
            loadPet();
        }
    }


    //
    @FXML protected void petSpeech( /* String text*/){
        System.out.println("I was pressed");

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), speechPane);
        fadeTransition.setCycleCount(2);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(Interpolator.EASE_IN);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }


    @FXML protected void onMenuClick () throws IOException{
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

    protected void loadPet(){
        sessionPet = petDao.getPet(sessionUserId);

        updateBar(hungerBar, sessionPet.getHunger());
        System.out.println("Hunger is " + sessionPet.getHunger());
        System.out.println("getter affection = " + sessionPet.getAffection());
        updateBar(energyBar, sessionPet.getEnergy());
        updateBar(affectionBar, sessionPet.getAffection());
        // need to add boredom bar here
        moodText.setText(sessionPet.getMoodLabel());
    }

    @FXML protected void updateBar(ProgressBar bar, double value){
        double clamped = Math.max(0.0, Math.min(1.0, value / 10));
        System.out.println("Value is " + clamped);
        bar.setProgress(clamped);
    }

    // duplicate code from Pet Stats controller
    private void startAutoRefresh() {
        refreshLoop = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> loadPet())
        );
        refreshLoop.setCycleCount(Timeline.INDEFINITE);
        refreshLoop.play();
    }

}
