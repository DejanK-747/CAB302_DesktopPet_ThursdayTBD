package com.cab302thursdaytbd.Model;

import java.util.HashMap;

public class Food {

    private String name;
    private HashMap<String, Integer> hungerChange = new HashMap<>();

    public Food(String name, String[] petType, int[] hungerStat){
        this.name = name;
        for (int i = 0; i < petType.length; i++){
            this.hungerChange.put(petType[i], hungerStat[i]);
        }
    }

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public int getHungerChangeForPet(String petType) { return hungerChange.get(petType);}
    public void addHungerChangeForPet( String petType, int hungerBoost) { this.hungerChange.put(petType, hungerBoost);}

}
