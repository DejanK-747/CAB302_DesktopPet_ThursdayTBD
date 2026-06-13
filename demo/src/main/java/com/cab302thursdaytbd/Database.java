package com.cab302thursdaytbd;

import java.io.File;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

 /**
 * Provides SQLite database connectivity and schema initialisation for the pet application.
 * The database file is created at {@code <working_directory>/database/petapp.sqlite}.
 */
public class Database {


    private static final String DB_FOLDER =
            System.getProperty("user.dir") + File.separator + "database";

    private static final String DB_URL =
            "jdbc:sqlite:" + DB_FOLDER + File.separator + "petapp.sqlite";


      /**
      * Opens and returns a new {@link Connection} to the SQLite database.
      * Creates the database directory if it does not already exist, and enables foreign key enforcement.
      *
      * @return a new {@link Connection} to the database
      * @throws SQLException if the connection cannot be established
      */
    public static Connection connect() throws SQLException {
        new File(DB_FOLDER).mkdirs();

        Connection conn = DriverManager.getConnection(DB_URL);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conn;
    }

      /**
      * Initialises the database schema, creating the {@code users} and {@code pets} tables
      * if they do not already exist. Enables foreign key support and prints the database path.
      * <p>
      * Table definitions:
      * <ul>
      *   <li>{@code users} — stores user accounts with a unique username and hashed password</li>
      *   <li>{@code pets} — stores pet records linked to a user, with stat columns constrained to 0–10</li>
      * </ul>
      */
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
                            + "affection INTEGER DEFAULT 10 CHECK (affection BETWEEN 0 AND 10),"
                            + "boredom INTEGER DEFAULT 10 CHECK (boredom BETWEEN 0 AND 10),"
                            + "last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,"
                            + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                            + ");"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

      /**
      * Deletes all rows from the {@code users} table.
      */
    public static void clearUsersTable() {
        String sql = "DELETE FROM users";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      /**
      * Deletes all rows from the {@code pets} table.
      */
    public static void clearPetsTable() {
        String sql = "DELETE FROM pets";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
