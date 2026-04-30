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

    private PetService petService;
    private PetDAO petDAO = new PetDAO();
    private int userId;


    // Mood tracking for "most common mood"
    private int happyTicks  = 0;
    private int sadTicks    = 0;
    private int boredTicks  = 0;
    private int hungryTicks = 0;
    private int tiredTicks  = 0;

    private Timeline refreshLoop;

    // Called from another controller AFTER loading this scene
    public void setUserId(int userId) {
        this.userId = userId;

        if (petService == null) {
            petService = new PetService(userId);
            petService.startDecay(() -> {
                Platform.runLater(() -> {
                    stop();
                    try {
                        Pet deadPet = petDAO.getPet(userId);
                        String reason = determineDeathReason(deadPet);

                        // delete from database immediately
                        petDAO.deletePet(userId);

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

        loadPet();
        startAutoRefresh();
    }

    // Loads pet data from DB and updates UI
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
        // Set pet image based on type
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

        // Update stat bars (hunger & energy are 0-10 in DB)
        updateBar(hungerBar, hungerPctLabel, pet.getHunger() / 10.0);
        updateBar(energyBar, energyPctLabel, pet.getEnergy() / 10.0);
        updateBar(affectionBar, affectionPctLabel, pet.getAffection() / 10.0);
        updateBar(boredomBar, boredomPctLabel, pet.getBoredom() / 10.0);

        // Compute current mood and update all mood labels
        String mood = computeMood(pet.getHunger(), pet.getEnergy(), pet.getAffection(), pet.getBoredom());
        moodLabel.setText(mood);
        updateMoodDisplay(mood);

        // Level based on pet id (placeholder until levelling system exists)
        levelLabel.setText("Lvl. " + Math.max(1, pet.getId()));
    }

    private void updateBar(ProgressBar bar, Label pctLabel, double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        bar.setProgress(clamped);
        pctLabel.setText((int)(clamped * 100) + "%");
    }

    // Refreshes the UI from the database every 2 seconds
    private void startAutoRefresh() {
        refreshLoop = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> loadPet())
        );
        refreshLoop.setCycleCount(Timeline.INDEFINITE);
        refreshLoop.play();
    }

    // Determines mood string from current stats
    private String computeMood(int hunger, int energy, double aff, double bore) {
        if (hunger <= 3) return "Hungry";
        if (energy <= 3) return "Tired";
        if (aff    <= 3) return "Sad";
        if (bore   <= 3) return "Bored";
        return "Happy";
    }

    // Determines death reason based on which stat hit zero
    private String determineDeathReason(Pet pet) {
        if (pet == null)          return "Unknown";
        if (pet.getHunger() <= 0) return "Starvation";
        if (pet.getEnergy() <= 0) return "Exhaustion";
        return "Unknown";
    }

    // Updates the current mood emoji in the pet card
    private void updateMoodDisplay(String mood) {
        switch (mood) {
            case "Happy":
                moodEmojiLabel.setText("😊");
                break;
            case "Hungry":
                moodEmojiLabel.setText("🍽️");
                break;
            case "Tired":
                moodEmojiLabel.setText("😴");
                break;
            case "Sad":
                moodEmojiLabel.setText("😢");
                break;
            case "Bored":
                moodEmojiLabel.setText("😐");
                break;
        }
    }

    // Finds which mood appeared most often and updates the most common mood section
    private void updateCommonMood() {
        String best = "Happy";
        int max = happyTicks;
        if (sadTicks    > max) { best = "Sad";    max = sadTicks; }
        if (boredTicks  > max) { best = "Bored";  max = boredTicks; }
        if (hungryTicks > max) { best = "Hungry"; max = hungryTicks; }
        if (tiredTicks  > max) { best = "Tired";  max = tiredTicks; }

        commonMoodLabel.setText(best);

        String desc;
        switch (best) {
            case "Sad":
                desc = "Needs more attention and love";
                break;
            case "Bored":
                desc = "Wants something fun to do";
                break;
            case "Hungry":
                desc = "Needs to be fed!";
                break;
            case "Tired":
                desc = "Needs to rest and play less";
                break;
            default:
                desc = "Feeling playful and content";
                break;
        }
        commonMoodDescLabel.setText(desc);

        String emoji;
        switch (best) {
            case "Sad":
                emoji = "😢";
                break;
            case "Bored":
                emoji = "😐";
                break;
            case "Hungry":
                emoji = "🍽️";
                break;
            case "Tired":
                emoji = "😴";
                break;
            default:
                emoji = "😊";
                break;
        }
        commonMoodEmojiLabel.setText(emoji);
    }

    // Stop all timers when leaving page
    public void stop() {
        if (refreshLoop != null) refreshLoop.stop();
        if (petService  != null) petService.stop();
    }

    // Change root file to main page file name when done
    @FXML
    private void handleBack() {
        stop();
        try {
            App.setRoot("main_page"); // temporary until main_pet exists
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}