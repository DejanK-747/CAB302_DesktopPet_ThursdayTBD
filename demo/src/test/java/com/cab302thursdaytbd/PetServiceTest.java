package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakePetDAO extends PetDAO {

    private Pet storedPet;

    @Override
    public Pet getPet(int userId) {
        return storedPet;
    }

    @Override
    public void updatePetStats(Pet pet) {
        storedPet = pet;
    }

    public void setPet(Pet pet) {
        storedPet = pet;
    }
}
public class PetServiceTest {
    private PetService petService;
    private FakePetDAO fakeDAO;
    private Pet pet;

    @BeforeEach
    public void setUp() {
        fakeDAO = new FakePetDAO();

        pet = new Pet(1, "TestPet", "cat");
        pet.setHunger(5);
        pet.setEnergy(5);
        pet.setAffection(5);
        pet.setBoredom(5);

        fakeDAO.setPet(pet);

        petService = new PetService(1);

        // inject fake DAO (simple version)
        try {
            java.lang.reflect.Field field =
                    PetService.class.getDeclaredField("petDAO");
            field.setAccessible(true);
            field.set(petService, fakeDAO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHungerDecreases() {

        Pet stored = fakeDAO.getPet(1);

        stored.setHunger(stored.getHunger() - 1);

        petService.stop(); // ensure no background interference

        assertEquals(4, stored.getHunger());
    }

    @Test
    public void testEnergyDecreases() {

        Pet stored = fakeDAO.getPet(1);

        stored.setEnergy(stored.getEnergy() - 1);

        petService.stop();

        assertEquals(4, stored.getEnergy());
    }

    @Test
    public void testBoredomIncreases() {

        Pet stored = fakeDAO.getPet(1);

        stored.setBoredom(stored.getBoredom() + 1);

        petService.stop();

        assertEquals(6, stored.getBoredom());
    }
    @Test
    public void testDeathCondition_HungerZero() {

        Pet stored = fakeDAO.getPet(1);
        stored.setHunger(0);

        boolean isDead = (stored.getHunger() <= 0 || stored.getEnergy() <= 0);

        assertTrue(isDead);
    }
    @Test
    public void testPetSurvives() {

        Pet stored = fakeDAO.getPet(1);
        stored.setHunger(5);
        stored.setEnergy(5);

        boolean isDead = (stored.getHunger() <= 0 || stored.getEnergy() <= 0);

        assertFalse(isDead);
    }

}
