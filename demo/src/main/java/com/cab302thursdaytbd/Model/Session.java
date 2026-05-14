package com.cab302thursdaytbd.Model;

public class Session {

    private static int userId = -1;
    private static String username;

    public static void setUser(int id, String name) {
        userId = id;
        username = name;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        userId = -1;
        username = null;
    }
}