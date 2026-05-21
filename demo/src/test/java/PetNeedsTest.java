import com.cab302thursdaytbd.Model.Pet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PetNeedsTest {
    @Test
    void petNeedsFoodWhenHungerIsLow() {
        Pet pet = new Pet(1, "frog", "Froggy");
        pet.setHunger(2);
        assertTrue(pet.needsFood());
    }

    @Test
    void petDoesNotNeedFoodWhenHungerIsHigh() {
        Pet pet = new Pet(1, "frog", "Froggy");
        pet.setHunger(8);
        assertFalse(pet.needsFood());
    }

    @Test
    void petNeedsAttentionWhenAffectionIsLow() {
        Pet pet = new Pet(1, "cat", "Milo");
        pet.setAffection(2);
        assertTrue(pet.needsAttention());
    }

    @Test
    void petDoesNotNeedAttentionWhenAffectionIsHigh() {
        Pet pet = new Pet(1, "cat", "Milo");
        pet.setAffection(8);
        assertFalse(pet.needsAttention());
    }

    @Test
    void petNeedsRestWhenEnergyIsLow() {
        Pet pet = new Pet(1, "monkey", "George");
        pet.setEnergy(2);
        assertTrue(pet.needsRest());
    }

    @Test
    void petDoesNotNeedRestWhenEnergyIsHigh() {
        Pet pet = new Pet(1, "monkey", "George");
        pet.setEnergy(8);
        assertFalse(pet.needsRest());
    }

    @Test
    void petNeedsPlayWhenBoredomIsHigh() {
        Pet pet = new Pet(1, "frog", "Froggy");
        pet.setBoredom(8);
        assertTrue(pet.needsPlay());
    }

    @Test
    void petDoesNotNeedPlayWhenBoredomIsLow() {
        Pet pet = new Pet(1, "frog", "Froggy");
        pet.setBoredom(2);
        assertFalse(pet.needsPlay());
    }
}
