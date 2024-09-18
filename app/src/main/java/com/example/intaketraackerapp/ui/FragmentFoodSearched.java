package com.example.intaketraackerapp.ui;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.JsonFunctions;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.intaketraackerapp.IntakeClasses.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentFoodSearched#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFoodSearched extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String foodname;
    private float foodCal;
    private float foodPro;
    private float foodCarbs;
    private float foodFats;
    TextView foodTitle;
    TextView calories;
    TextView proteins;
    TextView carbs;
    TextView fats;
    Button buttonAdd;

    private TextView mQuantityTextView;
    private Button mDecrementButton;
    private Button mIncrementButton;

    private int mQuantity = 0;


    public FragmentFoodSearched() {
        // Required empty public constructor
    }

    public static FragmentFoodSearched newInstance(String param1, String param2) {
        FragmentFoodSearched fragment = new FragmentFoodSearched();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.foodname = (String) this.getArguments().getString("foodClickedName");
            this.foodCal = (Float) this.getArguments().getFloat("foodClickedCal");
            this.foodPro = (Float) this.getArguments().getFloat("foodClickedPro");
            this.foodFats = (Float) this.getArguments().getFloat("foodClickedFats");
            this.foodCarbs = (Float) this.getArguments().getFloat("foodClickedCarbs");
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getActivity(), HomePageActivity.class);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_searched, container, false);

        foodTitle = (TextView) view.findViewById(R.id.foodnameTitleAdd);
        calories = (TextView) view.findViewById(R.id.textCaloriesAdd);
        proteins = (TextView) view.findViewById(R.id.textProteinsAdd);
        carbs = (TextView) view.findViewById(R.id.textCarbsAdd);
        fats = (TextView) view.findViewById(R.id.textFatsAdd);
        buttonAdd = (Button) view.findViewById(R.id.button_add);

        mQuantityTextView = view.findViewById(R.id.quantity_text_view);
        mDecrementButton = view.findViewById(R.id.decrement_button);
        mIncrementButton = view.findViewById(R.id.increment_button);

        foodTitle.setText(foodname);
        calories.setText(String.valueOf(foodCal) + " kcal");
        proteins.setText(String.valueOf(foodPro) + " g");
        fats.setText(String.valueOf(foodFats) + " g");
        carbs.setText(String.valueOf(foodCarbs) + " g");

        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuantity > 0) {
                    mQuantity--;
                    mQuantityTextView.setText(Integer.toString(mQuantity));
                }
            }
        });

        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantity++;
                mQuantityTextView.setText(Integer.toString(mQuantity));
            }
        });

        // Agregar TextWatcher al TextView para actualizar el valor del número cuando se cambia el texto
        mQuantityTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se requiere ninguna acción antes de que se cambie el texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Actualizar el valor del número cuando se cambia el texto
                try {
                    mQuantity = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    mQuantity = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se requiere ninguna acción después de que se cambia el texto
            }
        });

        /*
        Gson gson = new Gson();
        String contenidoJSON;
        try {
            InputStreamReader archivo = new InputStreamReader(getActivity().openFileInput("usuario.json"));
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

        //Creacion del Usuario a partir del JSON
        User user = gson.fromJson(contenidoJSON, User.class);*/


        LocalDate dateConsumed = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateConsumed = LocalDate.now();
        }
        String date = dateConsumed.toString();


        JsonFunctions jsonFunctions = new JsonFunctions();
        User user = jsonFunctions.readJson(getActivity());
        //Food foodSearched = new Food(this.foodname,this.foodCal,this.foodFats,this.foodCarbs,this.foodPro, date);
        //user.addFoodSearched(foodSearched);
        //jsonFunctions.writeJson(user,getActivity());

        final boolean[] petition = {false};
        String[] resSearchedFood = {""};
        do {
            String url = Utils.serverUri + "/getSearchedFood?user_id=" + user.getId();
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
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            resSearchedFood[0] = response.body().string();
                            System.out.println(resSearchedFood[0]);
                            petition[0] = true;
                        }
                    } finally {
                        response.close();
                    }
                }
            });
        }while(petition[0] == false);

        boolean foodexists = false;

        if(!resSearchedFood[0].equals("") && !resSearchedFood[0].equals("[]\r") && !resSearchedFood[0].equals("[]\r\n")) {
            Type listType2 = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
            Gson gson = new Gson();
            ArrayList<ArrayList<String>> arrayList = gson.fromJson(resSearchedFood[0], listType2);

            for(int i=0; i<arrayList.size(); i++) {
                String food = arrayList.get(i).get(1).toString();
                if(Objects.equals(this.foodname, food)){
                    foodexists = true;
                }
            }

            if(foodexists == false) {
                String url2 = Utils.serverUri + "/addSearchedFood";

                OkHttpClient client2 = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("param1", this.foodname)
                        .add("param2", user.getId())
                        .build();

                Request request2 = new Request.Builder()
                        .url(url2)
                        .post(requestBody)
                        .build();

                client2.newCall(request2).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (response.isSuccessful()) {

                                // Realiza las operaciones necesarias con la respuesta del servidor
                            }
                        } finally {
                            response.close();
                        }
                    }
                });

            }

        } else {
            String url2 = Utils.serverUri + "/addSearchedFood";

            OkHttpClient client2 = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("param1", this.foodname)
                    .add("param2", user.getId())
                    .build();

            Request request2 = new Request.Builder()
                    .url(url2)
                    .post(requestBody)
                    .build();

            client2.newCall(request2).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {

                            // Realiza las operaciones necesarias con la respuesta del servidor
                        }
                    } finally {
                        response.close();
                    }
                }
            });
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocalDate dateConsumed = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dateConsumed = LocalDate.now();
                }
                String date = dateConsumed.toString();

                if (mQuantity > 1){
                    foodname = foodname + "  x" + Integer.toString(mQuantity);
                    Food food = new Food(foodname,foodCal * mQuantity,foodFats * mQuantity,foodCarbs * mQuantity,foodPro * mQuantity,date);
                    user.addFoodConsumed(food);

                    boolean petitionDone = false;


                    String url = Utils.serverUri + "/addFoodConsumed";

                    OkHttpClient client = new OkHttpClient.Builder()
                                            .connectTimeout(2, TimeUnit.SECONDS)
                                            .writeTimeout(5, TimeUnit.SECONDS)
                                            .readTimeout(5, TimeUnit.SECONDS)
                                            .build();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("param1", user.getId())
                            .add("param2", date)
                            .add("param3", food.getName())
                            .add("param4", food.getCalories().toString())
                            .add("param5", food.getProteins().toString())
                            .add("param6", food.getFats().toString())
                            .add("param7", food.getCarbs().toString())
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

                                    // Realiza las operaciones necesarias con la respuesta del servidor
                                }
                            } finally {
                                response.close();
                            }
                        }
                    });

                    Toast toast1 = Toast.makeText(getActivity(), "Foods added successfully!", Toast.LENGTH_LONG);
                    toast1.show();

                    Intent intent = new Intent(getContext(), HomePageActivity.class);
                    startActivity(intent);

                    /*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getContext(), HomePageActivity.class);
                            startActivity(intent);
                        }
                    }, 2000);*/

                }else if(mQuantity == 1){
                    Food food = new Food(foodname,foodCal,foodFats,foodCarbs,foodPro,date);
                    user.addFoodConsumed(food);


                    boolean petitionDone = false;


                    String url = Utils.serverUri + "/addFoodConsumed";

                    OkHttpClient client = new OkHttpClient.Builder()
                                            .connectTimeout(2, TimeUnit.SECONDS)
                                            .writeTimeout(5, TimeUnit.SECONDS)
                                            .readTimeout(5, TimeUnit.SECONDS)
                                            .build();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("param1", user.getId())
                            .add("param2", date)
                            .add("param3", food.getName())
                            .add("param4", food.getCalories().toString())
                            .add("param5", food.getProteins().toString())
                            .add("param6", food.getFats().toString())
                            .add("param7", food.getCarbs().toString())
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

                                    // Realiza las operaciones necesarias con la respuesta del servidor
                                }
                            } finally {
                                response.close();
                            }
                        }
                    });

                    Toast toast1 = Toast.makeText(getActivity(), "Food added successfully!", Toast.LENGTH_LONG);
                    toast1.show();

                    Intent intent = new Intent(getContext(), HomePageActivity.class);
                    startActivity(intent);

                    /*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getContext(), HomePageActivity.class);
                            startActivity(intent);
                        }
                    }, 2000);*/

                }else if(mQuantity == 0) {
                    Toast toast1 = Toast.makeText(getActivity(), "Select a quantity value", Toast.LENGTH_SHORT);
                    toast1.show();
                }

                /*
                for (int i = 0; i < mQuantity; i++) {
                    user.addFoodConsumed(food);
                }*/

                /*
                String json = gson.toJson(user);
                try{
                    OutputStreamWriter archivo = new OutputStreamWriter(getActivity().openFileOutput("usuario.json", Context.MODE_PRIVATE));
                    archivo.write(json);
                    archivo.flush();
                    archivo.close();

                }catch (IOException e){
                    e.printStackTrace();
                }*/

                jsonFunctions.writeJson(user,getActivity());

            }
        });

        return view;
    }
}