package com.cab302thursdaytbd.Model;

import com.cab302thursdaytbd.Database;
import com.cab302thursdaytbd.Service.PasswordService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO implements IUserDAO {
    // Registers a new user in database
    @Override
    public int registerUser(String username, String password) {
        // SQL query used to insert a new user
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        // Hash the password before storing it for security
        String hashedPassword = PasswordService.hashPassword(password);

        try (Connection conn = Database.connect();
             // PreparedStatement inserts values into the SQL query
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Replace the first ?, ? with the username and hashed password
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            // Execute the INSERT statement
            int affectedRows = pstmt.executeUpdate();

            // Registration failed
            if (affectedRows == 0) {
                return -1;
            }

            // get generated user ID
            var rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Checks if the user's login details are correct
    @Override
    public int loginUser(String username, String password) {

        // Finds user by username
        String sql = "SELECT user_id, password_hash FROM users WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            // If a matching username exists
            if (rs.next()) {
                // Get the stored password hash from the database
                String storedHash = rs.getString("password_hash");
                // Hash the password entered by the user
                String inputHash = PasswordService.hashPassword(password);
                // Compare the stored hash with the entered password hash
                if (storedHash.equals(inputHash)) {
                    return rs.getInt("user_id");
                }
            }

            return -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}

