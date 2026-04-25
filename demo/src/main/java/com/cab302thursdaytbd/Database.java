package com.cab302thursdaytbd;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL =
            "jdbc:sqlite:demo/database/petapp.sqlite";

    public static Connection connect() throws SQLException {

        new File(System.getProperty("user.dir"), "database").mkdirs();
        return DriverManager.getConnection(DB_URL);

    }
}
