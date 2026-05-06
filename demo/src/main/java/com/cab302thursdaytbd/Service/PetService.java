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

    public void startDecay(Runnable onDeath) {
        decayLoop = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    Pet pet = petDAO.getPet(userId);

                    if (pet != null) {
                        int currentHunger = pet.getHunger();
                        int currentEnergy = pet.getEnergy();
                        int currentAffection = pet.getAffection();
                        int currentBoredom = pet.getBoredom();

                        pet.setHunger(currentHunger - 1);
                        pet.setEnergy(currentEnergy - 1);
                        pet.setAffection(currentAffection - 1);
                        pet.setBoredom(currentBoredom + 1);

                        petDAO.updatePetStats(pet);

                        if (pet.getHunger() <= 0 || pet.getEnergy() <= 0) {
                            decayLoop.stop(); // stop loop
                            onDeath.run();    // trigger death event
                        }
                    }
                })
        );

        decayLoop.setCycleCount(Timeline.INDEFINITE);
        decayLoop.play();
    }

    public Image[] getFrames(String petType){
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
}
