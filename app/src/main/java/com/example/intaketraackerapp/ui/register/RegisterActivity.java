package com.example.intaketraackerapp.ui.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.FoodList;
import com.example.intaketraackerapp.IntakeClasses.User;

import com.example.intaketraackerapp.R;
import com.example.intaketraackerapp.ui.login.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.intaketraackerapp.IntakeClasses.Utils;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private Button registerButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private User user;

    private Activity activity;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Add implementation here if needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Enable the register button if all fields are filled out, disable it otherwise
            registerButton.setEnabled(
                    nameEditText.getText().length() > 0 &&
                            heightEditText.getText().length() > 0 &&
                            weightEditText.getText().length() > 0
            );
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Add implementation here if needed
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.name);
        heightEditText = findViewById(R.id.height);
        weightEditText = findViewById(R.id.weight);
        registerButton = findViewById(R.id.login);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);


        // Disable the register button until all fields have been filled out
        registerButton.setEnabled(false);

        // Set up a text change listener for each EditText field
        nameEditText.addTextChangedListener(textWatcher);
        heightEditText.addTextChangedListener(textWatcher);
        weightEditText.addTextChangedListener(textWatcher);
        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        // Set up the click listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values entered by the user
                String name = nameEditText.getText().toString();
                String heightString = heightEditText.getText().toString();
                String weightString = weightEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validate the inputs
                if (!isValidEmail(username)) {
                    usernameEditText.setError("Please enter a valid email address");
                    Toast.makeText(RegisterActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters long");
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(heightString) || TextUtils.isEmpty(weightString)) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert the height and weight strings to floats
                float height = Float.parseFloat(heightString);
                float weight = Float.parseFloat(weightString);


                String url = Utils.serverUri + "/register";

                OkHttpClient client = new OkHttpClient.Builder()
                                            .connectTimeout(2, TimeUnit.SECONDS)
                                            .writeTimeout(5, TimeUnit.SECONDS)
                                            .readTimeout(5, TimeUnit.SECONDS)
                                            .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("param1",name)
                        .add("param2", username)
                        .add("param3", password)
                        .add("param4", heightString)
                        .add("param5", weightString)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Utils.showToast("Could not connect with the server, please try again later", activity);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (response.isSuccessful()) {
                                String responseBody = response.body().string();
                                Utils.showToast("User successfully registered", activity);
                            }
                        } finally {
                            response.close();
                        }
                    }
                });


                // Create a new user object with the entered data

                /*
                User user = new User(0,name,username,height, weight);

                LocalDate dateConsumed = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dateConsumed = LocalDate.now();
                }

                String date = dateConsumed.toString();

               Food food1 = new Food("Hamburger", 2300F, 56F, 44F, 15F, "2023-04-24");
               Food food2 = new Food("Sandwitch", 500F, 20F, 12F, 7F, "2023-03-26");
               Food food3 = new Food("Apple", 150F, 23F, 2F, 5F, "2023-03-20");
               Food food4 = new Food("Bread", 600F, 5F, 80F, 13F, "2022-04-25");
               Food food5 = new Food("Pizza", 1800F, 150F, 3F, 8F, "2022-04-20");
               Food food6 = new Food("Ice-Cream", 650F, 32F, 12F, 4F, "2022-04-20");
               Food food7 = new Food("Donut", 780F, 84F, 2F, 1F, "2022-04-20");
               user.addFoodConsumed(food1);
               user.addFoodConsumed(food2);
               user.addFoodConsumed(food3);
               user.addFoodConsumed(food4);
               user.addFoodConsumed(food5);
               user.addFoodConsumed(food6);
               user.addFoodConsumed(food7);*/


                // Finish the activity and start LoginActivity
                finish();

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                /*
                Bundle bundle = new Bundle();
                bundle.putSerializable("usuarioname",user.getName());
                bundle.putParcelableArrayList("consumed", user.getConsumedFood());
                bundle.putSerializable("height", user.getHeight());
                bundle.putSerializable("weight", user.getWeight());
                intent.putExtras(bundle);*/


                startActivity(intent);
            }
        });
    }

    // Function to validate email format
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}

