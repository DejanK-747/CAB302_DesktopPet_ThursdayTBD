package com.cab302thursdaytbd.Model;

public class Pet {

    private int id;
    private int userId;
    private String petName;
    private String petType;
    public int hunger;
    public int energy;
    public boolean isDead;

    public Pet(int userId, String petType, String petName) {
        this.userId = userId;
        this.petType = petType;
        this.petName = petName;
        this.hunger = 10;
        this.energy = 10;
        this.isDead = false;
    }

    // --- ID ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // --- User ---
    public int getUserId() {
        return userId;
    }

    // --- Name / Type ---
    public String getPetName() {
        return petName;
    }

    public String getPetType() {
        return petType;
    }

    // --- Stats ---
    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) { this.hunger = Math.min(hunger, 10);}

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, 10);
    }

    // --- Death ---
    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }
}
