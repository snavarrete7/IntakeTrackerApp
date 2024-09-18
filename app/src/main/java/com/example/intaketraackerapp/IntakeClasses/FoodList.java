package com.example.intaketraackerapp.IntakeClasses;

import java.util.ArrayList;

public class FoodList {
    private ArrayList<Food> lista;

    public FoodList() {
    }

    public FoodList(ArrayList<Food> lista) {
        this.lista = lista;
    }

    public ArrayList<Food> getLista() {
        return lista;
    }
}
