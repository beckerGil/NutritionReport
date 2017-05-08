package com.gil_becker.nutritionreport;

/**
 * Created by Gil-B on 07/03/2017.
 */

public class ConsumedFood {
    int id;
    String name;
    int date;
    int amount;

    public ConsumedFood(int id,
                        String name,
                        int date,
                        int amount) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ConsumedFood{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }
}
