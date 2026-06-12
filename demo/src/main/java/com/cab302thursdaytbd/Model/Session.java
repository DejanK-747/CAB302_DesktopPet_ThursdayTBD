package com.cab302thursdaytbd.Model;
/**
 * Maintains information about the currently logged-in user.
 * This class uses static fields and methods so that user
 * session data can be accessed throughout the application.
 */
public class Session {

    private static int userId = -1;
    private static String username;

    /**
     * Stores the details of the authenticated user.
     * @param id The unique ID of the logged-in user.
     * @param name The username of the logged-in user.
     */
    public static void setUser(int id, String name) {
        userId = id;
        username = name;
    }
    /**
     * Gets the ID of the currently logged-in user.
     * @return The user's ID, or -1 if no user is logged in.
     */
    public static int getUserId() {
        return userId;
    }
    /**
     * Gets the username of the currently logged-in user.
     * @return The username, or null if no user is logged in.
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Clears all stored session information.
     * This method is called when a user logs out.
     */
    public static void clear() {
        userId = -1;
        username = null;
    }
}