package com.cab302thursdaytbd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    public void adoptPet(int userId, String petType, String petName) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be a real user ID");
        }

        String sql = "INSERT OR REPLACE INTO pets(user_id, pet_type, pet_name, hunger, energy, is_dead) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, petType);
            stmt.setString(3, petName);
            stmt.setInt(4, 10);      // hunger default
            stmt.setInt(5, 10);      // energy default
            stmt.setInt(6, 0);       // is_dead (false = 0)

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Pet getPet(int userId) {
        String sql = "SELECT * FROM pets WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                Pet pet = new Pet(
                        rs.getInt("user_id"),
                        rs.getString("pet_type"),
                        rs.getString("pet_name")
                );

                // IMPORTANT: restore full state
                pet.setId(rs.getInt("id"));
                pet.setHunger(rs.getInt("hunger"));
                pet.setEnergy(rs.getInt("energy"));
                pet.setDead(rs.getInt("is_dead") == 1);

                return pet;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updatePetStats(Pet pet) {
        String sql = "UPDATE pets SET hunger = ?, energy = ?, is_dead = ? WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pet.getHunger());
            stmt.setInt(2, pet.getEnergy());
            stmt.setInt(3, pet.isDead() ? 1 : 0);
            stmt.setInt(4, pet.getUserId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePet(int userId) {
        String sql = "DELETE FROM pets WHERE user_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}