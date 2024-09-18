package com.example.intaketraackerapp.IntakeClasses;
import java.util.Calendar;
import java.util.Date;

public class CurrentDate {

    Date currentTime;

    public CurrentDate() {
        this.currentTime = Calendar.getInstance().getTime();
    };

    public Date getCurrentTime() {
        return currentTime;
    }
}
