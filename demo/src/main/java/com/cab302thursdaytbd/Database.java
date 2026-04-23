package com.cab302thursdaytbd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL =
            "jdbc:sqlite:" + System.getProperty("user.dir")
                    + "/src/main/resources/com/cab302thursdaytbd/database/petapp.sqlite";

    public static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite driver not found", e);
        }


        return DriverManager.getConnection(DB_URL);
    }
}
