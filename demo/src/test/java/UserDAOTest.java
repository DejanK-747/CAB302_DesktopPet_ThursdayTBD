import com.cab302thursdaytbd.Database;
import com.cab302thursdaytbd.Model.UserDAO;
import com.cab302thursdaytbd.Model.IUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

public class UserDAOTest {
    private IUserDAO userDAO;
    String username = "test_" + System.currentTimeMillis();

    @BeforeAll
    static void setupDatabase() {
        Database.initDatabase();
    }

    @BeforeEach
    void setup() {
        Database.clearUsersTable();
        userDAO = new UserDAO();
    }

    @Test
    void testRegisterUser() {
        String username = "test_" + System.currentTimeMillis();

        int userId = userDAO.registerUser(username, "password123");

        assertTrue(userId > 0);
    }

    @Test
    void testLoginSuccess() {
        String username = "test_" + System.currentTimeMillis();
        String password = "password123";

        userDAO.registerUser(username, password);

        int userId = userDAO.loginUser(username, password);

        assertTrue(userId > 0);
    }

    @Test
    void testLoginFailWrongPassword() {
        String username = "test_" + System.currentTimeMillis();

        userDAO.registerUser(username, "correctpass");

        int userId = userDAO.loginUser(username, "wrongpass");

        assertEquals(-1, userId);
    }
}
