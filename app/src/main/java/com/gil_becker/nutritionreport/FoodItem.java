package com.gil_becker.nutritionreport;

/**
 * Created by Gil-B on 07/03/2017.
 */

public class FoodItem {
    int id;
    String name;
    float calories;
    float fat;
    float protein;
    float carbs;
    float vitaminC;
    float vitaminA;
    float zinc;

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", calories=" + calories +
                ", fat=" + fat +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", vitaminC=" + vitaminC +
                ", vitaminA=" + vitaminA +
                ", zinc=" + zinc +
                '}';
    }

    public FoodItem(int id,
                    String name,
                    float calories,
                    float fat,
                    float protein,
                    float carbs,
                    float vitaminC,
                    float vitaminA,
                    float zinc) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.fat = fat;
        this.protein = protein;
        this.carbs = carbs;
        this.vitaminC = vitaminC;
        this.vitaminA = vitaminA;
        this.zinc = zinc;
    }

}
