package com.cab302thursdaytbd;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.cab302thursdaytbd.Model.Pet;

public class PetStatsLogicTest {

    @Test
    void returnsHungryWhenHungerLow() {

        PetStatsController controller = new PetStatsController();

        String mood = controller.computeMood(
                2,  // hunger
                10, // energy
                10, // affection
                10  // boredom
        );

        assertEquals("Hungry", mood);
    }
    @Test
    void returnsTiredWhenEnergyLow() {
        PetStatsController controller = new PetStatsController();

        String mood = controller.computeMood(10, 2, 10, 10);

        assertEquals("Tired", mood);
    }
    @Test
    void returnsSadWhenAffectionLow() {
        PetStatsController controller = new PetStatsController();

        String mood = controller.computeMood(10, 10, 2, 10);

        assertEquals("Sad", mood);
    }
    @Test
    void returnsStarvationWhenHungerZero() {

        PetStatsController controller = new PetStatsController();

        Pet pet = new Pet(1, "cat", "testPet");
        pet.setHunger(0);
        pet.setEnergy(10);

        String reason = controller.determineDeathReason(pet);

        assertEquals("Starvation", reason);
    }
}

