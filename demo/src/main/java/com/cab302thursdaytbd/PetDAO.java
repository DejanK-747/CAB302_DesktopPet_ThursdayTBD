package com.cab302thursdaytbd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PetDAO {
    public void adoptPet(int userId, String petType, String petName) {
        String sql = "INSERT OR REPLACE INTO pets(user_id, pet_type, pet_name) VALUES(?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, petType);
            stmt.setString(3, petName);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
