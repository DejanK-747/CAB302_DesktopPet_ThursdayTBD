package com.cab302thursdaytbd.Model;

import com.cab302thursdaytbd.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PetDAO implements IPetDAO {
    @Override public void adoptPet(int userId, String petType, String petName) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be a real user ID");
        }

        String sql = "INSERT OR REPLACE INTO pets(user_id, pet_type, pet_name, hunger, energy, affection, boredom, is_dead) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, petType);
            stmt.setString(3, petName);
            stmt.setInt(4, 10);      // hunger default
            stmt.setInt(5, 10);      // energy default
            stmt.setInt(6, 10); // affection (or whatever default you want)
            stmt.setInt(7, 0);  // boredom
            stmt.setInt(8, 0);       // is_dead (false = 0)

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public Pet getPet(int userId) {
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
                pet.setAffection(rs.getInt("affection"));
                pet.setBoredom(rs.getInt("boredom"));
                pet.setDead(rs.getInt("is_dead") == 1);

                return pet;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override public void updatePetStats(Pet pet) {
        String sql = "UPDATE pets SET hunger = ?, energy = ?, affection = ?, boredom = ?, is_dead = ? WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pet.getHunger());
            stmt.setInt(2, pet.getEnergy());
            stmt.setInt(3, pet.getAffection());
            stmt.setInt(4, pet.getBoredom());
            stmt.setInt(5, pet.isDead() ? 1 : 0);
            stmt.setInt(6, pet.getUserId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public void deletePet(int userId) {
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