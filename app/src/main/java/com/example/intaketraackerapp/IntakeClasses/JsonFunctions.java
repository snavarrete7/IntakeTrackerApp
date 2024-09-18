package com.example.intaketraackerapp.IntakeClasses;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class JsonFunctions {

    public JsonFunctions() {
    }

    public void writeJson(User user, Activity activity){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(user);
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(activity.openFileOutput("usuario.json", Context.MODE_PRIVATE));
            archivo.write(json);
            archivo.flush();
            archivo.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public User readJson(Activity activity){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String contenidoJSON;
        try {
            InputStreamReader archivo = new InputStreamReader(activity.openFileInput("usuario.json"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            contenidoJSON = "";
            while (linea != null) {
                contenidoJSON = contenidoJSON + linea + "\n";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        User user = gson.fromJson(contenidoJSON, User.class);

        return user;
    }

}
