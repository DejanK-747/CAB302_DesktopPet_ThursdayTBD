package com.cab302thursdaytbd;


import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Model.Session;
import com.cab302thursdaytbd.Service.FoodService;
import com.cab302thursdaytbd.Service.PetService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class MainPageController {

    private PetDAO petDao = new PetDAO();
    private PetService petService;
    private FoodService foodService = new FoodService();

    private Pet sessionPet;
    private int sessionUserId;


    @FXML private ProgressBar hungerBar;
    @FXML private ProgressBar affectionBar;
    @FXML private ProgressBar energyBar;
    @FXML private ProgressBar boredomBar;

    @FXML private Text moodText;
    @FXML private Text petName;
    @FXML private Text levelText;

    @FXML private ImageView petView;
    private int currentFrame;
    private Image[] frames;

    @FXML private Label statusChangeLabel;
    @FXML private Label needsLabel;
    @FXML private Pane speechPane;

    @FXML private ImageView bananaView;
    @FXML private ImageView grasshopperView;
    @FXML private ImageView mysteriousLiquidView;
    @FXML private ImageView biscuitView;

    private ParallelTransition statusChangePopUpAnim = new ParallelTransition();

    @FXML private Pane foodPopUp;

    @FXML private Pane petPane;

    private Timeline petAnimation;
    private Timeline refreshLoop;
    private Timeline decayLoop;



    //----------Testing
    private double initialMouseAnchorX;
    private double initialMouseAnchorY;
    private double initialNodeAnchorX;
    private double initialNodeAnchorY;
    //------------


    @FXML public void initialize() {
        sessionUserId = Session.getUserId();
        sessionPet = petDao.getPet(sessionUserId);
        petService = new PetService(sessionUserId);

        draggableFood(bananaView, "banana");
        draggableFood(grasshopperView, "grasshopper");
        draggableFood(mysteriousLiquidView, "mysteriousLiquid");
        draggableFood(biscuitView, "biscuit");


        frames = petService.getIdleFrames(sessionPet.getPetType());
        petView.setImage(frames[currentFrame]);

        playPetAnimation();
        loadPet();
        petName.setText(sessionPet.getPetName());

        // TO-DO: initialize map to pet sprite image



        // Duplicate code from Pet Stats. should be moved to PetService later
        petService.startDecay(() -> {
            Platform.runLater(() -> {
                try {
                    Pet deadPet = petDao.getPet(sessionUserId);
                    petAnimation.stop();
                    petService.stop();
                    String reason = petService.determineDeathReason(deadPet);


                    FXMLLoader loader = new FXMLLoader(App.class.getResource("pet_death.fxml"));
                    Parent root = loader.load();
                    PetDeathController deathController = loader.getController();
                    deathController.initDeathScreen(deadPet, reason);
                    App.getScene().setRoot(root);

                    // delete pet AFTER death screen
                    petDao.deletePet(sessionUserId);
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
    @FXML protected void showFoodPopUp() {
        showPopUp(foodPopUp);
    }


    protected void showPopUp(Pane popUp) {
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
    public void foodBoost(String foodType){
        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {
            int currentHunger = sessionPet.getHunger();
            String petType = sessionPet.getPetType();
            statusChangePopUp("Hunger");

            int hungerChange = foodService.getFood(foodType).getHungerChangeForPet(petType);

            sessionPet.setHunger(currentHunger + hungerChange);

            petAnimation.stop();

            if (hungerChange > 0) {
                frames = petService.getExcitedFrames(petType);
            } else {
                frames = petService.getAngryFrames(petType);
            }

            petAnimation = new Timeline(
                    new KeyFrame(Duration.millis(300), e -> {
                        currentFrame = (currentFrame + 1) % frames.length;
                        petView.setImage(frames[currentFrame]);
                    })
            );

            petAnimation.setCycleCount(3);
            petAnimation.setOnFinished(e -> playPetAnimation());
            petAnimation.play();


            petDao.updatePetStats(sessionPet);
            loadPet();
        }
    }

    @FXML protected void brushPet() {
        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {
            int currentEnergy = sessionPet.getEnergy();
            int currentBoredom = sessionPet.getBoredom();
            statusChangePopUp("Status Up");

            sessionPet.setEnergy(currentEnergy + 2);
            sessionPet.setBoredom(currentBoredom - 1);

            petDao.updatePetStats(sessionPet);
            loadPet();
        }
    }

    @FXML protected void strokePet() {
        if (statusChangePopUpAnim.getCurrentRate() == 0.0d) {

            int currentAffection = sessionPet.getAffection();
            int currentBoredom = sessionPet.getBoredom();

            statusChangePopUp("Status Up");

            sessionPet.setAffection(currentAffection + 2);
            sessionPet.setBoredom(currentBoredom - 1);

            petDao.updatePetStats(sessionPet);
            loadPet();
        }
    }

    //
    @FXML protected void petSpeech( /* String text*/){
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
        updateBar(energyBar, sessionPet.getEnergy());
        updateBar(affectionBar, sessionPet.getAffection());
        updateBar(boredomBar, sessionPet.getBoredom());

        updateNeedsLabel();
    }

    @FXML protected void updateBar(ProgressBar bar, double value){
        double clamped = Math.max(0.0, Math.min(1.0, value / 10));
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
    @FXML
    protected void handleGoChatButtonAction(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("conversation_page.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(newRoot);
        stage.show();
        stage.setResizable(false);
    }


    public void draggableFood(Node foodImg, String foodType) {
        foodImg.setOnMousePressed(mouseEvent ->{
            initialMouseAnchorX = mouseEvent.getX();
            initialMouseAnchorY = mouseEvent.getY();

            initialNodeAnchorX = foodImg.getLayoutX();
            initialNodeAnchorY = foodImg.getLayoutY();
        });

        foodImg.setOnMouseDragged(mouseEvent ->{
            foodImg.setLayoutX(mouseEvent.getSceneX() - initialMouseAnchorX - foodImg.getParent().getLayoutX());
            foodImg.setLayoutY(mouseEvent.getSceneY() - initialMouseAnchorY - foodImg.getParent().getLayoutY());
        });

        foodImg.setOnMouseReleased(mouseEvent ->{
            foodImg.setLayoutX(initialNodeAnchorX);
            foodImg.setLayoutY(initialNodeAnchorY);

            Point2D mouseLoc = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            Rectangle2D petPaneBounds = new Rectangle2D(petPane.getLayoutX(), petPane.getLayoutY(), petPane.getWidth(), petPane.getHeight());
            if (petPaneBounds.contains(mouseLoc)){
                foodBoost(foodType);
            }
        });
    }

    public void playPetAnimation() {


        String petType = sessionPet.getPetType();

        if (sessionPet != null) {
            petAnimation = new Timeline(
                    new KeyFrame(Duration.millis(300), e -> {


                        if (sessionPet == null) return;
                        String mood = sessionPet.getMoodLabel();

                        switch (mood) {
                            case "Angry": {
                                frames = petService.getAngryFrames(petType);
                                break;
                            }
                            case "Sad": {
                                frames = petService.getSadFrames(petType);
                                break;
                            }
                            case "Excited": {
                                frames = petService.getExcitedFrames(petType);
                                break;
                            }
                            case "Happy": {
                                frames = petService.getIdleFrames(petType);
                                break;
                            }
                            case "Sleepy": {
                                frames = petService.getSleepyFrames(petType);
                                break;
                            }
                            default: {
                                frames = petService.getIdleFrames(petType);
                            }
                        }

                        currentFrame = (currentFrame + 1) % frames.length;
                        petView.setImage(frames[currentFrame]);
                    })
            );

            if (sessionPet == null){
                petAnimation.stop();
                return;
            }

            petAnimation.setCycleCount(Timeline.INDEFINITE);
            petAnimation.play();
        }
    }
    private void updateNeedsLabel() {
        if (sessionPet.needsFood()) {
            needsLabel.setText("Your pet is hungry!");
            needsLabel.setVisible(true);
        } else if (sessionPet.needsAttention()) {
            needsLabel.setText("Your pet needs attention!");
            needsLabel.setVisible(true);
        } else if (sessionPet.needsRest()) {
            needsLabel.setText("Your pet is tired!");
            needsLabel.setVisible(true);
        } else if (sessionPet.needsPlay()) {
            needsLabel.setText("Your pet is bored!");
            needsLabel.setVisible(true);
        } else {
            needsLabel.setVisible(false);
        }
    }
}
