package com.cab302thursdaytbd.Service;

import com.cab302thursdaytbd.Model.Food;

public class FoodService {
    private final String[] petTypes = {"frog", "monkey"};

    private final Food banana = new Food("Banana", petTypes, new int[]{1, 3});
    private final Food grasshopper = new Food("Grasshopper", petTypes, new int[] {3, 1});
    private final Food mysteriousLiquid = new Food("Mysterious Liquid", petTypes, new int[] { -2, -2});
    private final Food biscuit = new Food("Biscuit", petTypes, new int[] {1, 1});

    public FoodService(){}

    public Food getFood(String foodName){
        Food foodToReturn;

        switch (foodName){
            case "banana":
                foodToReturn = banana;
                break;
            case "grasshopper":
                foodToReturn = grasshopper;
                break;
            case "mysteriousLiquid":
                foodToReturn = mysteriousLiquid;
                break;
            case "biscuit":
                foodToReturn = biscuit;
                break;
            default:
                foodToReturn = null;
        }

        return foodToReturn;
    }
}
