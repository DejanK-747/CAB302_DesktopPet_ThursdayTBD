package com.cab302thursdaytbd.Model;

import com.cab302thursdaytbd.Database;
import com.cab302thursdaytbd.Service.PasswordService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO that provides database operations related to user accounts.
 * This class is responsible for registering new users and
 * authenticating existing users against the database.
 */
public class UserDAO implements IUserDAO {
    /**
     * Registers a new user in the database.
     * The supplied password is hashed using SHA-256 before
     * being stored. If registration is successful, the
     * generated user ID is returned.
     * @param username The username for the new account.
     * @param password The user's plain-text password.
     * @return The generated user ID if registration succeeds, otherwise -1.
     */
    @Override
    public int registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        String hashedPassword = PasswordService.hashPassword(password);

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

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

    /**
     * Authenticates a user using their username and password.
     * The stored password hash is retrieved from the database
     * and compared with the hash of the supplied password.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return The user's ID if authentication succeeds, otherwise -1.
     */
    @Override
    public int loginUser(String username, String password) {

        String sql = "SELECT user_id, password_hash FROM users WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = PasswordService.hashPassword(password);
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

