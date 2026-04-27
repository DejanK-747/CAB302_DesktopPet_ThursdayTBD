package com.cab302thursdaytbd;

import java.io.File;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    private static final String DB_FOLDER =
            System.getProperty("user.dir") + File.separator + "database";

    private static final String DB_URL =
            "jdbc:sqlite:" + DB_FOLDER + File.separator + "petapp.sqlite";

    public static Connection connect() throws SQLException {
        new File(DB_FOLDER).mkdirs();

        Connection conn = DriverManager.getConnection(DB_URL);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conn;
    }

    public static void initDatabase() {
        System.out.println(DB_FOLDER + File.separator + "petapp.sqlite");
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users ("
                            + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "username TEXT UNIQUE NOT NULL,"
                            + "password_hash TEXT NOT NULL,"
                            + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                            + ");"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS pets ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "user_id INTEGER NOT NULL,"
                            + "pet_name TEXT NOT NULL,"
                            + "pet_type TEXT NOT NULL,"
                            + "hunger INTEGER DEFAULT 10 CHECK (hunger BETWEEN 0 AND 10),"
                            + "energy INTEGER DEFAULT 10 CHECK (energy BETWEEN 0 AND 10),"
                            + "is_dead INTEGER DEFAULT 0,"
                            + "last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,"
                            + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                            + ");"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TEST USER - delete when login system exists
    public static int ensureTestUser() {

        String selectSql = "SELECT user_id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";

        try (Connection conn = connect()) {

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, "testuser");

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("user_id");
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, "testuser");
                insertStmt.setString(2, "dev-password");

                insertStmt.executeUpdate();

                try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}