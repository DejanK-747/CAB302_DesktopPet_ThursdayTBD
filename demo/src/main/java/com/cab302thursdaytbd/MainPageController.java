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
import javafx.scene.control.Button;
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

/**
 * The controller for the main page
 */
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

    @FXML private Text petName;

    @FXML private ImageView petView;
    private int currentFrame;
    private Image[] frames;

    @FXML private Label statusChangeLabel;
    @FXML private Label needsLabel;

    @FXML private ImageView bananaView;
    @FXML private ImageView grasshopperView;
    @FXML private ImageView mysteriousLiquidView;
    @FXML private ImageView biscuitView;

    @FXML private ImageView petBrush;
    @FXML private ImageView hand;

    private ParallelTransition statusChangePopUpAnim = new ParallelTransition();

    @FXML private Pane foodPane;
    @FXML private Pane brushPane;
    @FXML private Pane pettingPane;

    @FXML private Pane petPane;

    private Timeline petAnimation;
    private Timeline refreshLoop;
    private Timeline foodFlashTimeline;
    private Timeline brushFlashTimeline;
    private Timeline strokeFlashTimeline;

    @FXML private Button foodButton;
    @FXML private Button brushButton;
    @FXML private Button strokeButton;


    //----------For Draggable Items
    private double initialMouseAnchorX;
    private double initialMouseAnchorY;
    private double initialNodeAnchorX;
    private double initialNodeAnchorY;

    private int brushCounter = 0;
    private int pettingCounter = 0;
    //------------

    /**
     * Used to initialize the user, the pet, certain UI elements and functions, and the pet status decay.
     */
    public void initialize() {
        sessionUserId = Session.getUserId();
        sessionPet = petDao.getPet(sessionUserId);
        petService = new PetService(sessionUserId);

        foodDropFunction(bananaView, "banana");
        foodDropFunction(grasshopperView, "grasshopper");
        foodDropFunction(mysteriousLiquidView, "mysteriousLiquid");
        foodDropFunction(biscuitView, "biscuit");

        brushDragFunction(petBrush);
        pettingDragFunction(hand);


        frames = petService.getIdleFrames(sessionPet.getPetType());
        petView.setImage(frames[currentFrame]);

        playPetAnimation();
        loadPet();
        petName.setText(sessionPet.getPetName());

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
        if (foodPane.getScaleX() == 1){
            closePopUp(foodPane);
        } else {
            showPopUp(foodPane);
            closePopUp(brushPane);
            closePopUp(pettingPane);
        }
    }

    @FXML protected void showBrushPopUp(){
        if (brushPane.getScaleX() == 1){
            closePopUp(brushPane);
        } else {
            showPopUp(brushPane);
            closePopUp(foodPane);
            closePopUp(pettingPane);
        }
    }

    @FXML protected void showPettingPopUp(){
        if (pettingPane.getScaleX() == 1){
            closePopUp(pettingPane);
        } else {
            showPopUp(pettingPane);
            closePopUp(foodPane);
            closePopUp(brushPane);
        }
    }


    /**
     * A method to show or hide a menu
     * @param popUp The menu pane to hide or show
     */
    protected void showPopUp(Pane popUp) {
        ScaleTransition transition = new ScaleTransition(Duration.seconds(0.25), popUp);
        transition.setToX(1);
        transition.setToY(1);
        transition.setInterpolator(Interpolator.LINEAR);

        transition.play();
    }
    //-----------------------------------------

    protected void closePopUp(Pane popUp){
        ScaleTransition transition = new ScaleTransition(Duration.seconds(0.25), popUp);
        transition.setToX(0);
        transition.setToY(0);
        transition.setInterpolator(Interpolator.LINEAR);

        transition.play();
    }

    /**
     * A method to indicate status change by showing a text pop-up
     * @param text The text to display in the pop-up text
     */
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

    /**
     * Changes the pet's hunger when given a certain food
     * @param foodType The food type to give to a pet
     */
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

    /**
     * Increase the pet's energy and reduce boredom by brushing the pet
     */
    protected void brushPet() {
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

    /**
     * Increase the pet's affection and decrease the boredom status by stroking the pet
     */
    protected void strokePet() {
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


    @FXML protected void onMenuClick () throws IOException{
        try {
            petService.stop();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("main_menu.fxml"));
            Parent root = loader.load();
            MainMenuController menuController = loader.getController();
            menuController.setUserId(Session.getUserId());
            App.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Retrieve pet's status from the database and update status bars
     */
    protected void loadPet(){
        sessionPet = petDao.getPet(sessionUserId);

        updateBar(hungerBar, sessionPet.getHunger());
        updateBar(energyBar, sessionPet.getEnergy());
        updateBar(affectionBar, sessionPet.getAffection());
        updateBar(boredomBar, sessionPet.getBoredom());

        updateNeedsLabel();
    }

    /**
     * Updates the status bar
     * @param bar The bar to change
     * @param value The value to set the bar to
     */
    protected void updateBar(ProgressBar bar, double value){
        double clamped = Math.max(0.0, Math.min(1.0, value / 10));
        bar.setProgress(clamped);
    }

    /**
     * Method to start a timeline loop to automatically keep loading the pet and updating the status bar
     */
    private void startAutoRefresh() {
        refreshLoop = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> loadPet())
        );
        refreshLoop.setCycleCount(Timeline.INDEFINITE);
        refreshLoop.play();
    }


    @FXML
    protected void handleGoChatButtonAction(ActionEvent event) throws IOException {
        petService.stop();
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("conversation_page.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(newRoot);
        stage.show();
        stage.setResizable(false);
    }


    public void draggableMaker(Node node) {
        node.setOnMousePressed(mouseEvent ->{
            initialMouseAnchorX = mouseEvent.getX();
            initialMouseAnchorY = mouseEvent.getY();

            initialNodeAnchorX = node.getLayoutX();
            initialNodeAnchorY = node.getLayoutY();
        });

        node.setOnMouseDragged(mouseEvent ->{
            node.setLayoutX(mouseEvent.getSceneX() - initialMouseAnchorX - node.getParent().getLayoutX());
            node.setLayoutY(mouseEvent.getSceneY() - initialMouseAnchorY - node.getParent().getLayoutY());
        });

        node.setOnMouseReleased(mouseEvent -> {
            node.setLayoutX(initialNodeAnchorX);
            node.setLayoutY(initialNodeAnchorY);
        });
    }

    public void foodDropFunction(Node foodNode, String foodType){
        draggableMaker(foodNode);

        foodNode.setOnMouseReleased(mouseEvent ->{
            foodNode.setLayoutX(initialNodeAnchorX);
            foodNode.setLayoutY(initialNodeAnchorY);

            Point2D mouseLoc = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            Rectangle2D petPaneBounds = new Rectangle2D(petPane.getLayoutX(), petPane.getLayoutY(), petPane.getWidth(), petPane.getHeight());
            if (petPaneBounds.contains(mouseLoc)){
                foodBoost(foodType);
            }
        });
    }

    public void brushDragFunction(Node brushNode){
        draggableMaker(brushNode);

        brushNode.setOnMouseDragged(mouseEvent ->{
            brushNode.setLayoutX(mouseEvent.getSceneX() - initialMouseAnchorX - brushNode.getParent().getLayoutX());
            brushNode.setLayoutY(mouseEvent.getSceneY() - initialMouseAnchorY - brushNode.getParent().getLayoutY());


            Point2D mouseLoc = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            Rectangle2D petPaneBounds = new Rectangle2D(petPane.getLayoutX(), petPane.getLayoutY(), petPane.getWidth(), petPane.getHeight());
            if (petPaneBounds.contains(mouseLoc)){
                brushCounter++;
                if (brushCounter >= 50){
                    brushPet();
                    brushCounter = 0;
                }
            }
        });
    }

    public void pettingDragFunction(Node handNode){
        draggableMaker(handNode);

        handNode.setOnMouseDragged(mouseEvent ->{
            handNode.setLayoutX(mouseEvent.getSceneX() - initialMouseAnchorX - handNode.getParent().getLayoutX());
            handNode.setLayoutY(mouseEvent.getSceneY() - initialMouseAnchorY - handNode.getParent().getLayoutY());


            Point2D mouseLoc = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            Rectangle2D petPaneBounds = new Rectangle2D(petPane.getLayoutX(), petPane.getLayoutY(), petPane.getWidth(), petPane.getHeight());
            if (petPaneBounds.contains(mouseLoc)){
                pettingCounter++;
                if (pettingCounter >= 50){
                    strokePet();
                    pettingCounter = 0;
                }
            }
        });
    }

    /**
     * Starts pet animation
     */
    protected void playPetAnimation() {


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
                            case "Sleepy":
                            case "Bored": {
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

    /**
     * Shows a label when a pet's status gets in critical condition
     */
    private void updateNeedsLabel() {

        if (sessionPet.needsFood()) {
            foodFlashTimeline = startFlashingButton(foodButton, foodFlashTimeline);
        } else {
            foodFlashTimeline = stopFlashingButton(foodButton, foodFlashTimeline);
        }

        if (sessionPet.needsRest()) {
            brushFlashTimeline = startFlashingButton(brushButton, brushFlashTimeline);
        } else {
            brushFlashTimeline = stopFlashingButton(brushButton, brushFlashTimeline);
        }

        if (sessionPet.needsPlay()) {
            strokeFlashTimeline = startFlashingButton(strokeButton, strokeFlashTimeline);
        } else {
            strokeFlashTimeline = stopFlashingButton(strokeButton, strokeFlashTimeline);
        }

        if (sessionPet.needsFood()) {
            needsLabel.setText("Your pet is hungry!");
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

    /**
     * Makes the status boosting function flash when certain pet statuses gets too low
     * @param button
     * @param timeline
     * @return
     */
    private Timeline startFlashingButton(Button button, Timeline timeline) {
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
            return timeline;
        }

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e ->
                        button.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-background-color: yellow;")
                ),
                new KeyFrame(Duration.seconds(0.5), e ->
                        button.setStyle("-fx-border-color: whitesmoke; -fx-border-width: 3; -fx-background-color: whitesmoke;")
                ),
                new KeyFrame(Duration.seconds(1), e ->
                        button.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-background-color: yellow;")
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        return timeline;
    }

    private Timeline stopFlashingButton(Button button, Timeline timeline) {
        if (timeline != null) {
            timeline.stop();
        }

        button.setStyle("");
        return null;
    }
}
