package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Service.PetService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class PetStatsController {

    //below are fxml bindings, these fields are all linked to elements in pet_stats.fxml
    // and allow the controller to update the ui


    // Pet card
    @FXML private Label nameLabel;
    @FXML private Label levelLabel;
    @FXML private Label moodLabel;
    @FXML private ImageView petView;

    // Most common mood
    @FXML private Label moodEmojiLabel;
    @FXML private Label commonMoodEmojiLabel;
    @FXML private Label commonMoodLabel;
    @FXML private Label commonMoodDescLabel;

    // Stat bars
    @FXML private ProgressBar hungerBar;
    @FXML private ProgressBar energyBar;
    @FXML private ProgressBar affectionBar;
    @FXML private ProgressBar boredomBar;

    // Percentage labels
    @FXML private Label hungerPctLabel;
    @FXML private Label energyPctLabel;
    @FXML private Label affectionPctLabel;
    @FXML private Label boredomPctLabel;

    private PetService petService; //handles the stat decay loop and writes changes to the database
    private PetDAO petDAO = new PetDAO(); // the data access object used to read and write pet data to the db
    private int userId; //currently logged in user id


    private Timeline refreshLoop; // this refresh loop reads from the db every two secs and then updates the ui
    //done separate from decay loop so the display stays in sync with the decay changes

    // this is the entry point. called by main page controller after loading this scene, this is how data is passed after the scene has loaded
    // sets the user id then starts the decay via pet service, and loads the pet data from the db. also starts the auto-refresh loop
    public void setUserId(int userId) {
        this.userId = userId;
        //this wil make it so that only one petservice instance is created (safeguard)
        if (petService == null) {
            petService = new PetService(userId);
            // starDecay() runs a timer that decrements the stats in the database every five seconds.
            // the death callback is being passed here, it fires when hunger or energy hit zero and the pet dies.
            petService.startDecay(() -> {
                Platform.runLater(() -> {
                    //platform run later ensures the ui update happens on the javafx thread, since the decay timer runs in the background
                    stop(); // this stops all timers before navigating
                    try {
                        // fetch the pet one last time to get its final state for the death screen
                        Pet deadPet = petDAO.getPet(userId);
                        String reason = petService.determineDeathReason(deadPet);

                        // delete from database immediately when pet dies
                        petDAO.deletePet(userId);
                        //load the death screen and pass the dead pet's data into it
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
        }

        loadPet(); //load and display the pets current stats
        startAutoRefresh(); // begin the two second ui refresh loop
    }

    // loads pet data from DB and updates UI
    // reads the pets current state from the db and updates all ui elements
    // this is called once on load and then every two seconds by the refresh loop
    // if the pet has been deleted, getpet will return null and the method will exit early
    private void loadPet() {
        Pet pet = petDAO.getPet(userId);
        if (pet == null) return;

        nameLabel.setText(pet.getPetName());
        //debug for stat values
        System.out.println(
                "STATS LOAD -> H=" + pet.getHunger() +
                        " E=" + pet.getEnergy() +
                        " A=" + pet.getAffection() +
                        " B=" + pet.getBoredom()
        );
        // set the pet image based on type chosen in pet select and stored in db
        String type = pet.getPetType();
        try {
            String imgPath;
            switch (type) {
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
            petView.setImage(new Image(getClass().getResource(imgPath).toExternalForm()));
        } catch (Exception ignored) {}

        // all the stats are stored from 0-10 in the db
        // dividing by 10 converts to the correct range that the progress bar expects.
        updateBar(hungerBar, hungerPctLabel, pet.getHunger() / 10.0);
        updateBar(energyBar, energyPctLabel, pet.getEnergy() / 10.0);
        updateBar(affectionBar, affectionPctLabel, pet.getAffection() / 10.0);
        updateBar(boredomBar, boredomPctLabel, pet.getBoredom() / 10.0);

        // Compute current mood and update all mood labels
        String mood = pet.getMoodLabel();
        moodLabel.setText(mood);
        //update mood emoji and description in the ui
        updateMoodDisplay(mood);
        updateCommonMood(mood);

        // level based on pet id (placeholder until levelling system exists)
        levelLabel.setText("Lvl. " + Math.max(1, pet.getId()));
    }
    // this method is a helper. it clamps values to the valid range (0.0-1.0), and updates the progress bar to display
    //the matching percentage text
    private void updateBar(ProgressBar bar, Label pctLabel, double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        bar.setProgress(clamped);
        pctLabel.setText((int)(clamped * 100) + "%");
    }

    // this method reloads the pet data from the database every two seconds. it uses timeline to do this.
    // timeline is used as a repeating timer, a keyframe defines an action that should occur after a specified duration
    // for this, every two seconds the load pet() method is called to refresh the ui with the latest pet data from the database
    //this will repeat endlessly until refreshloop.stop() is called.
    private void startAutoRefresh() {
        refreshLoop = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> loadPet())
        );
        refreshLoop.setCycleCount(Timeline.INDEFINITE);
        refreshLoop.play();
    }


    // updates the current mood emoji shown on the stats ui
    private void updateMoodDisplay(String mood) {
        switch (mood) {
            case "Happy":
                moodEmojiLabel.setText("😊");
                break;
            case "Angry":
                moodEmojiLabel.setText("😡");
                break;
            case "Sleepy":
                moodEmojiLabel.setText("😴");
                break;
            case "Excited":
                moodEmojiLabel.setText("😆");
                break;
            case "Sad":
                moodEmojiLabel.setText("😢");
                break;
        }
    }

    // finds which mood appeared most often and updates the most common mood section in ui
    private void updateCommonMood(String mood) {
        commonMoodLabel.setText(mood);

        // mood description text
        String desc;
        switch (mood) {
            case "Sad":
                desc = "Needs more attention and love";
                break;
            case "Sleepy":
                desc = "Wants something fun to do";
                break;
            case "Angry":
                desc = "Someone needs their needs met!";
                break;
            case "Excited":
                desc = "Having the time of their life!";
                break;
            default:
                desc = "Feeling playful and content";
                break;
        }
        commonMoodDescLabel.setText(desc);

        String emoji;
        switch (mood) {
            case "Happy":
                emoji ="😊";
                break;
            case "Angry":
                emoji ="😡";
                break;
            case "Sleepy":
                emoji ="😴";
                break;
            case "Excited":
                emoji ="😆";
                break;
            case "Sad":
                emoji ="😢";
                break;
            default :
                emoji ="This shouldn't happen";
                break;
        }
        commonMoodEmojiLabel.setText(emoji);
    }

    // stops all timers when leaving page or scene
    public void stop() {
        if (refreshLoop != null) refreshLoop.stop();
        if (petService  != null) petService.stop();
    }

    // this is the function that handles when the back button on the ui is pressed. will return to the main page.
    @FXML
    private void handleBack() {
        stop();
        try {
            App.setRoot("main_page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}