import com.cab302thursdaytbd.Database;
import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Model.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;


public class PetDAOTest {

    @BeforeAll
    static void setupDatabase() {
        Database.initDatabase();
    }

    @BeforeEach void populateUsers() {
        UserDAO.registerUser("test1", "password123");
        UserDAO.registerUser("test2", "password123");
        UserDAO.registerUser("test3", "password123");
    }


    @BeforeEach
    void setup() {
        Database.clearUsersTable();
        Database.clearPetsTable();
    }

    @Test
    void testAdoptAndGetPet(){
        PetDAO petDAO = new PetDAO();

        int testUserId = UserDAO.loginUser("test1", "password123");

        petDAO.adoptPet(testUserId, "monkey", "Jim");

        Pet userOnePet = petDAO.getPet(testUserId);


        assertEquals(testUserId, userOnePet.getUserId());
        assertEquals("monkey", userOnePet.getPetType());
        assertEquals("Jim", userOnePet.getPetName());
    }

    @Test
    void testAdoptAndGetPetMultipleUsers(){
        PetDAO petDAO = new PetDAO();

        int testUserOneId = UserDAO.loginUser("test1", "password123");
        int testUserTwoId = UserDAO.loginUser("test2", "password123");
        int testUserThreeId = UserDAO.loginUser("test3", "password123");


        petDAO.adoptPet(testUserOneId, "monkey", "Jim");
        petDAO.adoptPet(testUserTwoId, "frog", "Jill");
        petDAO.adoptPet(testUserThreeId, "monkey", "Jake");


        Pet userOnePet = petDAO.getPet(testUserOneId);
        Pet userTwoPet = petDAO.getPet(testUserTwoId);
        Pet userThreePet = petDAO.getPet(testUserThreeId);


        assertEquals(testUserOneId, userOnePet.getUserId());
        assertEquals(testUserTwoId, userTwoPet.getUserId());
        assertEquals(testUserThreeId, userThreePet.getUserId());
    }

    @Test
    void testUpdatePet(){
        PetDAO petDAO = new PetDAO();
        int testUserId = UserDAO.loginUser("test1", "password123");

        petDAO.adoptPet(testUserId, "frog", "Bartholomew");

        Pet newPet = petDAO.getPet(testUserId);

        newPet.setAffection(1);
        newPet.setBoredom(4);
        newPet.setHunger(8);
        newPet.setEnergy(9);

        petDAO.updatePetStats(newPet);

        Pet updatedPet = petDAO.getPet(testUserId);

        assertEquals(1, updatedPet.getAffection());
        assertEquals(4, updatedPet.getBoredom());
        assertEquals(8, updatedPet.getHunger());
        assertEquals(9, updatedPet.getEnergy());

    }

    @Test
    void testDeletePet(){
        PetDAO petDAO = new PetDAO();
        int testUserId = UserDAO.loginUser("test1", "password123");

        petDAO.adoptPet(testUserId, "frog", "Bartholomew");

        petDAO.deletePet(testUserId);

        assertNull(petDAO.getPet(testUserId));
    }




}
