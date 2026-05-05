package com.cab302thursdaytbd.Model;

import com.cab302thursdaytbd.Database;
import com.cab302thursdaytbd.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public static int registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        String hashedPassword = PasswordUtil.hashPassword(password);

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            int affectedRows = pstmt.executeUpdate();

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

    public static int loginUser(String username, String password) {

        String sql = "SELECT user_id, password_hash FROM users WHERE username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                String inputHash = PasswordUtil.hashPassword(password);

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

