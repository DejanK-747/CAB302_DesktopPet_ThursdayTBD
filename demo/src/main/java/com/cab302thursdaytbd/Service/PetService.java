package com.cab302thursdaytbd.Service;

import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class PetService {
    private PetDAO petDAO = new PetDAO();
    private int userId;
    private Timeline decayLoop;

    public PetService(int userId) {
        this.userId = userId;
    }

    public void stop() {
        if (decayLoop != null) {
            decayLoop.stop();
        }
    }

    private int tickCount = 0;

    public void startDecay(Runnable onDeath) {
        decayLoop = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {    // decay stats Hunger, Affection, Energy (cleanliness)
                    Pet pet = petDAO.getPet(userId);

                    if (pet != null) {
                        tickCount++;

                        if (tickCount % 3 == 0) {
                            pet.setHunger(pet.getHunger() - 1);
                        }

                        if (tickCount % 4 == 0) {
                            pet.setEnergy(pet.getEnergy() - 1);
                        }

                        if (tickCount % 6 == 0) {
                            pet.setAffection(pet.getAffection() - 1);
                        }

                        petDAO.updatePetStats(pet);

                        if (pet.getHunger() <= 0 || pet.getEnergy() <= 0) {
                            decayLoop.stop();
                            onDeath.run();
                        }
                    }
                })
        );

        decayLoop.setCycleCount(Timeline.INDEFINITE);
        decayLoop.play();
    }

    public Image[] getIdleFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }

    public Image[] getExcitedFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-excited1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-excited2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }

    public Image[] getSadFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sad1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sad2.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sad3.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sad1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sad2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }

    public Image[] getAngryFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-angry1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-angry2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-angry1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-angry2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }

    public Image[] getSleepyFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sleep1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sleep2.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sleep2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sleep1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sleep2.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sleep2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }
}
