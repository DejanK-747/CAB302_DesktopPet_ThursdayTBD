package com.cab302thursdaytbd.Model;

/**
 * Interface for the Pet Data Access Object that handles
 * the CRUD operations for the Contact class with the database.
 */
public interface IPetDAO {

    public void adoptPet(int userId, String petType, String petName);

    public Pet getPet(int userId);

    public void updatePetStats(Pet pet);

    public void deletePet(int userId);

}
