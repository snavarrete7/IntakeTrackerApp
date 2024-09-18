package com.example.intaketraackerapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.intaketraackerapp.IntakeClasses.Utils;

public class ModifyProfileActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editName;
    private EditText editWeight;
    private EditText editHeight;
    private EditText editPassword;
    private EditText currentPassword;

    private String petition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_profile);

        editEmail = findViewById(R.id.editEmail);
        editName = findViewById(R.id.editName);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        editPassword = findViewById(R.id.editPassword);
        currentPassword = findViewById(R.id.CurrentPassword);

        // Obtener los valores actuales del usuario y mostrarlos en los campos EditText correspondientes
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
        String currentEmail = user.getEmail();
        String currentName = user.getName();
        String currentWeight = Float.toString(user.getWeight());
        String currentHeight = Float.toString(user.getHeight());

        editEmail.setText(currentEmail);
        editName.setText(currentName);
        editWeight.setText(currentWeight);
        editHeight.setText(currentHeight);

        Button btnConfirmChanges = findViewById(R.id.btnConfirmChanges);
        btnConfirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User usuario = user;
                // Obtener los nuevos valores de los campos EditText y actualizar los datos del usuario en la base de datos
                String newEmail = editEmail.getText().toString().trim();
                String newName = editName.getText().toString().trim();
                String newWeight = editWeight.getText().toString().trim();
                String newHeight = editHeight.getText().toString().trim();
                String newPassword = editPassword.getText().toString().trim();
                //TODO:String currentPass = currentPassword.getText().toString().trim();

                // Verificar si la nueva contraseña tiene una longitud de al menos 6 caracteres
                if (newPassword.length() < 6) {
                    Toast.makeText(ModifyProfileActivity.this, "Password should have at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Comprobar si se han proporcionado nuevos valores para cada campo y actualizarlos en la base de datos
                /*TODO: if (!newEmail.equals("")) {
                    updateUserEmailInDatabase(newEmail);
                }*/
                if (!newEmail.equals("")) {
                    usuario.setEmail(newEmail);
                } else {
                    Toast.makeText(ModifyProfileActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newName.equals("")) {
                    usuario.setName(newName);
                } else {
                    Toast.makeText(ModifyProfileActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newWeight.equals("")) {
                    usuario.setWeight(Float.parseFloat(newWeight));
                } else {
                    Toast.makeText(ModifyProfileActivity.this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newHeight.equals("")) {
                    usuario.setHeight(Float.parseFloat(newHeight));
                } else {
                    Toast.makeText(ModifyProfileActivity.this, "Please enter a height", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*TODO:if (!newPassword.equals("")) {
                    updatePasswordInDatabase(newPassword, currentPass);
                }*/



                // Modifica los valores del objeto JSON existente con los valores ingresados por el usuario
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

                final boolean[] petitionUpdateDone = {false};
                do {
                    String url = Utils.serverUri + "/updateUser";

                    OkHttpClient client = new OkHttpClient.Builder()
                                            .connectTimeout(2, TimeUnit.SECONDS)
                                            .writeTimeout(5, TimeUnit.SECONDS)
                                            .readTimeout(5, TimeUnit.SECONDS)
                                            .build();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("param1", usuario.getId())
                            .add("param2", usuario.getName())
                            .add("param3", usuario.getEmail())
                            .add("param4", newPassword)
                            .add("param5", String.valueOf(98))
                            .add("param6", String.valueOf(98))
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                if (response.isSuccessful()) {
                                    petition = response.body().string();
                                    System.out.println(petition);
                                    petitionUpdateDone[0] = true;
                                    // Realiza las operaciones necesarias con la respuesta del servidor
                                }
                            } finally {
                                response.close();
                            }
                        }
                    });
                }while(petitionUpdateDone[0] == false);

                // Mostrar un mensaje de confirmación y cerrar la actividad
                Toast.makeText(ModifyProfileActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ModifyProfileActivity.this, HomePageActivity.class);
                        startActivity(intent);
                    }
                }, 2000); // mostrar el mensaje durante 2 segundos antes de iniciar la actividad de perfil
            }
        });
    }
}
