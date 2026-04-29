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

    // Stats tracked locally (0-10 scale, matching hunger/energy in Pet)
    private double affection = 10.0;
    private double boredom = 0.0;

    // Mood tracking for "most common mood"
    private int happyTicks  = 0;
    private int sadTicks    = 0;
    private int boredTicks  = 0;
    private int hungryTicks = 0;
    private int tiredTicks  = 0;

    private Timeline refreshLoop;
    private Timeline decayLoop;

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
        startDecay();
    }

    // Loads pet data from DB and updates UI
    private void loadPet() {
        Pet pet = petDAO.getPet(userId);
        if (pet == null) return;

        nameLabel.setText(pet.getPetName());

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
        updateBar(affectionBar, affectionPctLabel, affection / 10.0);
        updateBar(boredomBar, boredomPctLabel, boredom / 10.0);

        // Compute current mood and update all mood labels
        String mood = computeMood(pet.getHunger(), pet.getEnergy(), affection, boredom);
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

    // Decays affection and boredom over time independently of hunger/energy
    private void startDecay() {
        decayLoop = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    affection = Math.max(0, affection - 0.5);
                    boredom   = Math.max(0, boredom   - 0.5);

                    // Track mood ticks for "most common mood"
                    Pet pet = petDAO.getPet(userId);
                    if (pet != null) {
                        String mood = computeMood(pet.getHunger(), pet.getEnergy(), affection, boredom);
                        switch (mood) {
                            case "Happy":
                                happyTicks++;
                                break;
                            case "Sad":
                                sadTicks++;
                                break;
                            case "Bored":
                                boredTicks++;
                                break;
                            case "Hungry":
                                hungryTicks++;
                                break;
                            case "Tired":
                                tiredTicks++;
                                break;
                        }
                        updateCommonMood();
                    }
                })
        );
        decayLoop.setCycleCount(Timeline.INDEFINITE);
        decayLoop.play();
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
        if (affection <= 0)       return "Neglect";
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

    // Called by other controllers to boost affection (e.g., after petting)
    public void boostAffection(double amount) {
        affection = Math.min(10, affection + amount);
    }

    // Called by other controllers to reduce boredom (e.g., after playing)
    public void reduceBoredom(double amount) {
        boredom = Math.min(10, boredom + amount);
    }

    // Stop all timers when leaving page
    public void stop() {
        if (refreshLoop != null) refreshLoop.stop();
        if (decayLoop   != null) decayLoop.stop();
        if (petService  != null) petService.stop();
    }

    // Change root file to main page file name when done
    @FXML
    private void handleBack() {
        stop();
        try {
            App.setRoot("pet_selection1"); // temporary until main_pet exists
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}