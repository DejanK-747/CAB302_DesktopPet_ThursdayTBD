package com.cab302thursdaytbd;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
                        pet.hunger -= 1;
                        pet.energy -= 1;

                        petDAO.updatePetStats(pet);

                        if (pet.hunger <= 0 || pet.energy <= 0) {
                            decayLoop.stop(); // stop loop
                            onDeath.run();    // trigger death event
                        }
                    }
                })
        );

        decayLoop.setCycleCount(Timeline.INDEFINITE);
        decayLoop.play();
    }
}
