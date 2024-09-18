package com.example.intaketraackerapp.IntakeClasses;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String name;
    private ArrayList<Food> consumedFood;
    private ArrayList<String> photosTakenPath;

    private ArrayList<Food> foodSearched;
    private float height;
    private float weight;
    private String id;

    private String email;
    private String password;

    public User(String id, String name, String email, float height, float weight) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.height = height;
        this.consumedFood = new ArrayList<Food>();
        this.photosTakenPath = new ArrayList<String>();
        this.foodSearched = new ArrayList<Food>();
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Food> getConsumedFood() {
        return consumedFood;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void addFoodConsumed(Food consumedFood) {
        this.consumedFood.add(consumedFood);
    }

    public void setConsumedFood(ArrayList<Food> consumedFood) {
        this.consumedFood = consumedFood;
    }

    public ArrayList<String> getPhotosTakenPath() {
        return photosTakenPath;
    }

    public void setPhotosTakenPath(ArrayList<String> photosTakenPath) {
        this.photosTakenPath = photosTakenPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Food> getFoodSearched() {
        return foodSearched;
    }

    public void setFoodSearched(ArrayList<Food> foodSearched) {
        this.foodSearched = foodSearched;
    }

    public void addFoodSearched(Food searchedFood) {
        this.foodSearched.add(searchedFood);
    }
}
