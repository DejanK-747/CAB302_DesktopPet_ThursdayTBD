package com.cab302thursdaytbd;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class PetStatsController {

    // Pet card
    @FXML private Label nameLabel;
    @FXML private Label levelLabel;
    @FXML private Label moodLabel;
    @FXML private ImageView petView;

    // Most common mood
    @FXML private Label moodEmojiLabel;
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

    private PetDAO petDAO = new PetDAO();
    private int userId;

    // Stats tracked locally (0–10 scale, matching hunger/energy in Pet)
    private double affection = 10.0;
    private double boredom = 0.0;   // boredom grows over time (inverse of engagement)

    // Mood tracking for "most common mood"
    private int happyTicks = 0;
    private int sadTicks   = 0;
    private int boredTicks = 0;

    private Timeline refreshLoop;
    private Timeline decayLoop;

    // Called from another controller AFTER loading this scene
    public void setUserId(int userId) {
        this.userId = userId;
        loadPet();
        startAutoRefresh();
        startDecay();
    }
    // temp initialise
    @FXML
    public void initialize() {
        // Hardcode userId = 1 for testing until main page exists
        setUserId(1);
    }
    // Loads pet data from DB and updates UI
    private void loadPet() {
        Pet pet = petDAO.getPet(userId);
        if (pet == null) return;

        nameLabel.setText(pet.getPetName());

        // Set pet image based on type
        String type = pet.getPetType();
        try {
            String imgPath = type.equals("cat")
                    ? "/com/cab302thursdaytbd/images/Cat-happy.png"
                    : "/com/cab302thursdaytbd/images/frog1.png";
            petView.setImage(new Image(getClass().getResource(imgPath).toExternalForm()));
        } catch (Exception ignored) {}

        // Update stat bars (hunger & energy are 0–10 in DB)
        updateBar(hungerBar, hungerPctLabel, pet.getHunger() / 10.0);
        updateBar(energyBar, energyPctLabel, pet.getEnergy() / 10.0);
        updateBar(affectionBar, affectionPctLabel, affection / 10.0);

        // Boredom bar: value grows, so we display it directly
        double boredomNorm = Math.min(boredom / 10.0, 1.0);
        updateBar(boredomBar, boredomPctLabel, boredomNorm);

        // Compute current mood and update all mood labels
        String mood = computeMood(pet.getHunger(), pet.getEnergy(), affection, boredom);
        moodLabel.setText(mood);
        updateMoodDisplay(mood);

        // Level: simple formula based on pet id or days alive (placeholder)
        levelLabel.setText("Lvl. " + Math.max(1, pet.getId()));
    }

    private void updateBar(ProgressBar bar, Label pctLabel, double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        bar.setProgress(clamped);
        pctLabel.setText((int)(clamped * 100) + "%");
    }

    // Decays affection and grows boredom over time independently of the main pet stats
    private void startDecay() {
        decayLoop = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    affection = Math.max(0, affection - 0.5);
                    boredom   = Math.min(10, boredom   + 0.5);

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
        if (hunger <= 3)   return "Hungry";
        if (energy <= 3)   return "Tired";
        if (aff <= 3)      return "Sad";
        if (bore >= 7)     return "Bored";
        return "Happy";
    }

    // Updates the current mood label and emoji
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

    // Finds which mood appeared most often and updates the section
    private void updateCommonMood() {
        String best = "Happy";
        int max = happyTicks;
        if (sadTicks   > max) { best = "Sad";   max = sadTicks; }
        if (boredTicks > max) { best = "Bored";  }

        commonMoodLabel.setText(best);
        String desc;
        switch (best) {
            case "Sad":
                desc = "Needs more attention and love";
                break;
            case "Bored":
                desc = "Wants something fun to do";
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
            default:
                emoji = "😊";
                break;
        }
        moodEmojiLabel.setText(emoji);
    }

    // Called by other controllers to boost affection (e.g., after petting)
    public void boostAffection(double amount) {
        affection = Math.min(10, affection + amount);
    }

    // Called by other controllers to reduce boredom (e.g., after playing)
    public void reduceBoredom(double amount) {
        boredom = Math.max(0, boredom - amount);
    }

    // Stop timers when leaving page
    public void stop() {
        if (refreshLoop != null) refreshLoop.stop();
        if (decayLoop   != null) decayLoop.stop();
    }
    // change root file to main page file name when done.
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