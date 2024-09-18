package com.example.intaketraackerapp.IntakeClasses;

import android.app.Activity;
import android.widget.Toast;

public class Utils {
    public static String serverUri = "http://192.168.1.38:8080";
    public static void showToast(String msg, Activity activity) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
