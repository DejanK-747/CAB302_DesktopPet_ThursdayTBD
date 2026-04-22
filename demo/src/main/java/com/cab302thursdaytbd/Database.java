package com.cab302thursdaytbd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL =
            "jdbc:sqlite:C:\\Users\\dd19o\\OneDrive - Queensland University of Technology\\Uni\\Year 3, Sem 1\\CAB302\\Project\\CAB302_DesktopPet_ThursdayTBD\\demo\\src\\main\\resources\\com\\cab302thursdaytbd\\database\\petapp.sqlite";

    public static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite driver not found", e);
        }


        return DriverManager.getConnection(DB_URL);
    }
}
