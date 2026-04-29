package com.cab302thursdaytbd;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    String username = "test_" + System.currentTimeMillis();

    @Test
    void testRegisterUser() {
        String username = "test_" + System.currentTimeMillis();

        int userId = UserDAO.registerUser(username, "password123");

        assertTrue(userId > 0);
    }

    @Test
    void testLoginSuccess() {
        String username = "test_" + System.currentTimeMillis();
        String password = "password123";

        UserDAO.registerUser(username, password);

        int userId = UserDAO.loginUser(username, password);

        assertTrue(userId > 0);
    }

    @Test
    void testLoginFailWrongPassword() {
        String username = "test_" + System.currentTimeMillis();

        UserDAO.registerUser(username, "correctpass");

        int userId = UserDAO.loginUser(username, "wrongpass");

        assertEquals(-1, userId);
    }
}
