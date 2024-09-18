package com.example.intaketraackerapp.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.FoodList;
import com.example.intaketraackerapp.IntakeClasses.JsonFunctions;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.databinding.ActivityLoginBinding;
import com.example.intaketraackerapp.ui.register.RegisterActivity;

import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.intaketraackerapp.IntakeClasses.Utils;

public class LoginActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    private EditText usernameEditText;
    private EditText passwordEditText;
    User user = null;

    private String userid;

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);


        usernameEditText = binding.username;
        passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button notRegisterButton = binding.notRegistered;
        final ProgressBar loadingProgressBar = binding.loading;

        notRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    //updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUiWithUser();
                /*
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());*/
            }
        });
    }

    private void updateUiWithUser() {

        //String welcome = getString(R.string.welcome) + model.getDisplayName();
        //Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        /*
        Bundle bundle = getIntent().getExtras();
        //String username = (String) bundle.getSerializable("usuarioname");
        ArrayList<Food> foodconsumed = (ArrayList<Food>) bundle.getSerializable("consumed");
        float height = (float) bundle.getSerializable("height");
        float weight = (float) bundle.getSerializable("weight");*/


        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);

        /*
        Bundle bundle2 = new Bundle();
        bundle.putSerializable("usuarioname",usernameEditText.getText().toString());
        bundle.putParcelableArrayList("consumed", foodconsumed);
        bundle.putSerializable("height", height);
        bundle.putSerializable("weight", weight);
        intent.putExtras(bundle);*/

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        String url = Utils.serverUri + "/login";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody = new FormBody.Builder()
                .add("param1",username)
                .add("param2", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.showToast("Please connect to an existing server", activity);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                userid = "It does not match";

                try {
                    String responsestring = response.body().string();
                    if (response.isSuccessful()) {
                        userid = responsestring;
                        System.out.println(userid);
                        // Realiza las operaciones necesarias con la respuesta del servidor

                        String useridCorrect = userid.replaceAll("\r", "").replaceAll("\n", "");

                        if (!useridCorrect.equals("It does not match")) {

                            String[] res = {""};
                            String url = Utils.serverUri + "/getUser?user_id=" + useridCorrect;
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(2, TimeUnit.SECONDS)
                                    .writeTimeout(5, TimeUnit.SECONDS)
                                    .readTimeout(5, TimeUnit.SECONDS)
                                    .build();

                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Utils.showToast("Failed to get user data from server: " + e.getMessage(), activity);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        if (response.isSuccessful()) {
                                            res[0] = response.body().string();

                                            String[] resFoodConsumed = {""};
                                            final boolean[] petition = {false};
                                            String url2 = Utils.serverUri + "/getFoodConsumed?user_id=" + useridCorrect;
                                            OkHttpClient client2 = new OkHttpClient.Builder()
                                                    .connectTimeout(2, TimeUnit.SECONDS)
                                                    .writeTimeout(5, TimeUnit.SECONDS)
                                                    .readTimeout(5, TimeUnit.SECONDS)
                                                    .build();

                                            Request request2 = new Request.Builder()
                                                    .url(url2)
                                                    .build();

                                            client2.newCall(request2).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    Utils.showToast("Failed to get food data of user from server: " + e.getMessage(), activity);
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    try {
                                                        if (response.isSuccessful()) {
                                                            resFoodConsumed[0] = response.body().string();

                                                            if (resFoodConsumed[0].equals("[]") || resFoodConsumed[0].equals("[]\r") || resFoodConsumed[0].equals("[]\r\n")) {
                                                                Type listType = new TypeToken<ArrayList<String>>() {
                                                                }.getType();
                                                                Gson gson2 = new Gson();
                                                                ArrayList<String> arrayListUser = gson2.fromJson(res[0], listType);


                                                                String name = arrayListUser.get(0);
                                                                String email = arrayListUser.get(1);
                                                                float altura = Float.parseFloat(arrayListUser.get(2));
                                                                float peso = Float.parseFloat(arrayListUser.get(3));

                                                                User newuser = new User(useridCorrect, name, email, altura, peso);
                                                                JsonFunctions jsonFunctions = new JsonFunctions();
                                                                jsonFunctions.writeJson(newuser, activity);
                                                            } else {
                                                                Type listType = new TypeToken<ArrayList<String>>() {
                                                                }.getType();
                                                                Gson gson2 = new Gson();
                                                                ArrayList<String> arrayListUser = gson2.fromJson(res[0], listType);

                                                                String name = arrayListUser.get(0);
                                                                String email = arrayListUser.get(1);
                                                                float altura = Float.parseFloat(arrayListUser.get(2));
                                                                float peso = Float.parseFloat(arrayListUser.get(3));

                                                                User newuser = new User(useridCorrect, name, email, altura, peso);

                                                                Type listType2 = new TypeToken<ArrayList<ArrayList<String>>>() {
                                                                }.getType();
                                                                Gson gson = new Gson();
                                                                ArrayList<ArrayList<String>> arrayList = gson.fromJson(resFoodConsumed[0], listType2);
                                                                System.out.println(arrayList);

                                                                for (int i = 0; i < arrayList.size(); i++) {

                                                                    String namefood = arrayList.get(i).get(2).toString();

                                                                    StringBuilder stringBuilder = new StringBuilder();

                                                                    if (hasConsecutivePercent20(namefood)) {
                                                                        int length = namefood.length();
                                                                        boolean previousIsPercent20 = false;
                                                                        for (int j = 0; j < length; j++) {
                                                                            char currentChar = namefood.charAt(j);
                                                                            if (currentChar == '%' && j + 2 < length && namefood.charAt(j + 1) == '2' && namefood.charAt(j + 2) == '0') {
                                                                                if (!previousIsPercent20) {
                                                                                    stringBuilder.append("   ");
                                                                                    previousIsPercent20 = true;
                                                                                }
                                                                                j += 2; // Salta los siguientes dos caracteres
                                                                            } else {
                                                                                stringBuilder.append(currentChar);
                                                                                previousIsPercent20 = false;
                                                                            }
                                                                        }
                                                                        namefood = stringBuilder.toString();
                                                                    }

                                                                    Float caloriesfood = Float.parseFloat(arrayList.get(i).get(3));
                                                                    Float proteinfood = Float.parseFloat(arrayList.get(i).get(4));
                                                                    Float fatfood = Float.parseFloat(arrayList.get(i).get(5));
                                                                    Float carbofood = Float.parseFloat(arrayList.get(i).get(6));
                                                                    String dateconsumedfood = arrayList.get(i).get(7).toString();

                                                                    Food newfood = new Food(namefood, caloriesfood, fatfood, carbofood, proteinfood, dateconsumedfood);
                                                                    newuser.addFoodConsumed(newfood);

                                                                }

                                                                JsonFunctions jsonFunctions = new JsonFunctions();
                                                                jsonFunctions.writeJson(newuser, activity);
                                                            }

                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    } finally {
                                                        response.close();
                                                    }
                                                }
                                            });

                                        }
                                    } finally {
                                        response.close();
                                    }
                                }
                            });
                        } else {
                            Utils.showToast("User does not exist or password is incorrect", activity);
                        }

                    } else {
                        onFailure(call, new IOException("Unexpected code: " + response));
                    }
                } catch (Exception e) {
                    Utils.showToast("No va el login" + e.getMessage(), activity);
                } finally {
                    response.close();
                }
                String string = "";
            }
        });
        /*try {
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (!responseBody.equals("")) {
                    try {
                        if (response.isSuccessful()) {
                            userid = response.body().string();
                            System.out.println(userid);
                            // Realiza las operaciones necesarias con la respuesta del servidor
                        }
                    } finally {
                        response.close();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error getting the context", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/



    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public static boolean hasConsecutivePercent20(String input) {
        int length = input.length();

        for (int i = 0; i < length - 2; i++) {
            if (input.charAt(i) == '%' && input.charAt(i + 1) == '2' && input.charAt(i + 2) == '0') {
                return true;
            }
        }

        return false;
    }
}