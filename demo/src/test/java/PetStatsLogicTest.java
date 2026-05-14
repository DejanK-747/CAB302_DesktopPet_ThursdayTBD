import com.cab302thursdaytbd.Model.Pet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PetStatsLogicTest {

    @Test
    void returnsAngryWhenHungerLow() {

        Pet pet = new Pet(1, "cat", "TestPet");

        pet.setHunger(2);
        pet.setEnergy(10);
        pet.setAffection(10);

        String mood = pet.getMoodLabel();

        assertEquals("Angry", mood);
    }

    @Test
    void returnsAngryWhenEnergyLow() {

        Pet pet = new Pet(1, "cat", "TestPet");

        pet.setHunger(10);
        pet.setEnergy(2);
        pet.setAffection(10);

        String mood = pet.getMoodLabel();

        assertEquals("Angry", mood);
    }

    @Test
    void returnsSadWhenAffectionLow() {

        Pet pet = new Pet(1, "cat", "TestPet");

        pet.setHunger(10);
        pet.setEnergy(10);
        pet.setAffection(2);

        String mood = pet.getMoodLabel();

        assertEquals("Sad", mood);
    }

    @Test
    void returnsSleepyWhenEnergyMediumLow() {

        Pet pet = new Pet(1, "cat", "TestPet");

        pet.setHunger(10);
        pet.setEnergy(4);
        pet.setAffection(5);

        String mood = pet.getMoodLabel();

        assertEquals("Sleepy", mood);
    }

    @Test
    void returnsExcitedWhenAllStatsHigh() {

        Pet pet = new Pet(1, "cat", "TestPet");

        pet.setHunger(9);
        pet.setEnergy(9);
        pet.setAffection(9);

        String mood = pet.getMoodLabel();

        assertEquals("Excited", mood);
    }

    @Test
    void returnsHappyForNormalStats() {

        Pet pet = new Pet(1, "cat", "TestPet");

        pet.setHunger(6);
        pet.setEnergy(6);
        pet.setAffection(6);

        String mood = pet.getMoodLabel();

        assertEquals("Happy", mood);
    }
}

