package com.example.intaketraackerapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ModifyWeightHeightActivity extends AppCompatActivity {
    private EditText pesoEditText;
    private EditText alturaEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_weight_height);

        pesoEditText = findViewById(R.id.editTextPeso);
        alturaEditText = findViewById(R.id.editTextAltura);

        // Lee el archivo JSON existente y obtén el objeto JSON
        Gson gson = new Gson();
        String contenidoJSON;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("usuario.json"));
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
        String currentWeight = Float.toString(user.getWeight());
        String currentHeight = Float.toString(user.getHeight());

        pesoEditText.setText(currentWeight);
        alturaEditText.setText(currentHeight);

        Button guardarButton = findViewById(R.id.btnGuardarCambios);
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Modifica los valores del objeto JSON existente con los valores ingresados por el usuario
                User usuario = user;
                String pesoString = pesoEditText.getText().toString().trim();
                float peso = Float.parseFloat(pesoString);
                usuario.setWeight(peso);
                String alturaString = alturaEditText.getText().toString().trim();
                float altura = Float.parseFloat(alturaString);
                usuario.setHeight(altura);

                String json = gson.toJson(usuario);

                // Escribe el objeto JSON modificado en el archivo JSON
                try {
                    OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("usuario.json", Context.MODE_PRIVATE));
                    archivo.write(json);
                    archivo.flush();
                    archivo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Regresa a la actividad perfil
                Intent intent = new Intent(ModifyWeightHeightActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}