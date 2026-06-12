package com.cab302thursdaytbd.Model;
/**
 * Interface that defines the operations required for user authentication.
 */
public interface IUserDAO {
    /**
     * Registers a new user account.
     * @param username The username to register.
     * @param password The user's password.
     * @return The ID of the newly created user if registration is successful, otherwise -1.
     */
    int registerUser(String username, String password);
    /**
     * Attempts to authenticate a user.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return The user's ID if authentication is successful, otherwise -1.
     */
    int loginUser(String username, String password);
}