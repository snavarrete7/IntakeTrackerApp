package com.example.intaketraackerapp.IntakeClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

public class Food implements Parcelable {

    private String name;
    private Float calories;
    private Float fats;
    private Float carbs;
    private Float proteins;
    private String dateConsumed;

    public Food() {

    }

    public Food(String name, Float calories, Float fats, Float carbs, Float proteins, String dateConsumed) {
        this.name = name;
        this.calories = calories;
        this.fats = fats;
        this.carbs = carbs;
        this.proteins = proteins;
        this.dateConsumed = dateConsumed;
    }

    protected Food(Parcel in) {
        name = in.readString();
        dateConsumed = in.readString();
        if (in.readByte() == 0) {
            calories = null;
        } else {
            calories = in.readFloat();
        }
        if (in.readByte() == 0) {
            fats = null;
        } else {
            fats = in.readFloat();
        }
        if (in.readByte() == 0) {
            carbs = null;
        } else {
            carbs = in.readFloat();
        }
        if (in.readByte() == 0) {
            proteins = null;
        } else {
            proteins = in.readFloat();
        }
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public String getName() {
        return name;
    }

    public Float getCalories() {
        return calories;
    }

    public Float getFats() {
        return fats;
    }

    public Float getCarbs() {
        return carbs;
    }

    public Float getProteins() {
        return proteins;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public void setFats(Float fats) {
        this.fats = fats;
    }

    public void setCarbs(Float carbs) {
        this.carbs = carbs;
    }

    public void setProteins(Float proteins) {
        this.proteins = proteins;
    }

    public void setDateConsumed(String dateConsumed) {
        this.dateConsumed = dateConsumed;
    }

    public String getDateConsumed() {
        return dateConsumed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(dateConsumed);
        if (calories == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(calories);
        }
        if (fats == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(fats);
        }
        if (carbs == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(carbs);
        }
        if (proteins == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(proteins);
        }
    }
}
