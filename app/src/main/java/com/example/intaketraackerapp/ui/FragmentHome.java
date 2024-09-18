package com.example.intaketraackerapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intaketraackerapp.IntakeClasses.CurrentDate;
import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.JsonFunctions;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import com.github.clans.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentStats fragmentStats = new FragmentStats();
    FragmentSearchPRUEBA fragmentSearchPRUEBA = new FragmentSearchPRUEBA();
    FragmentFoodListItem fragmentFoodListItem = new FragmentFoodListItem();
    FragmentSearchPrueba2 fragmentSearchPrueba2 = new FragmentSearchPrueba2();
    FragmentCamera fragmentCamera = new FragmentCamera();
    FloatingActionMenu fabMenu;
    com.github.clans.fab.FloatingActionButton fabSearch;
    com.github.clans.fab.FloatingActionButton fabCamera;


    // TODO: Rename and change types of parameters

    private String username;
    private ArrayList<Food> foodConsumed;
    private float height;
    private float weight;

    TextView calories;
    TextView proteins;
    TextView carbs;
    TextView fats;
    TextView date;
    TextView petition;

    private ListView listView;




    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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
            this.username = (String) this.getArguments().getSerializable("usuarioname");
            this.foodConsumed = (ArrayList<Food>) this.getArguments().getSerializable("consumed");
            this.height = (float) this.getArguments().getSerializable("height");
            this.weight = (float) this.getArguments().getSerializable("weight");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        calories = (TextView) view.findViewById(R.id.textCalories);
        proteins = (TextView) view.findViewById(R.id.textProteins);
        carbs = (TextView) view.findViewById(R.id.textCarbs);
        fats = (TextView) view.findViewById(R.id.textFats);
        date = (TextView) view.findViewById(R.id.date);

        fabMenu = (FloatingActionMenu) view.findViewById(R.id.floating_menu);
        fabSearch = (FloatingActionButton) view.findViewById(R.id.floating_search_button);
        fabCamera = (FloatingActionButton) view.findViewById(R.id.floating_camera_button);

        listView = (ListView) view.findViewById(R.id.foodList);
        fabMenu.close(true);

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(fragmentSearchPRUEBA);

            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(fragmentCamera);
            }
        });

        JsonFunctions jsonFunctions = new JsonFunctions();
        User user = jsonFunctions.readJson(getActivity());

        //MOSTRAR FECHA
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        date.setText(currentDate.toString());

        float cal = 0, pro = 0, car = 0, fat = 0;
        ArrayList<Food> foodList = user.getConsumedFood();
        ArrayList<String> foodListNames = new ArrayList<String>();

        if (foodList != null) {
            for (int i = 0; i < foodList.size(); i++) {
                Food food = foodList.get(i);
                if(food.getDateConsumed().equals(currentDate.toString())){
                    foodListNames.add(food.getName());
                    cal = food.getCalories() + cal;
                    pro = food.getProteins() + pro;
                    car = food.getCarbs() + car;
                    fat = food.getFats() + fat;
                }
            }
            DecimalFormat df = new DecimalFormat("#.##");
            calories.setText(String.valueOf(df.format(cal)) + " kcal");
            proteins.setText(String.valueOf(df.format(pro)) + " g");
            carbs.setText(String.valueOf(df.format(car)) + " g");
            fats.setText(String.valueOf(df.format(fat)) + " g");

            //LISTA DE ALIMENTOS
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item, foodListNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object foodname = listView.getItemAtPosition(i);
                    String foodname2 = foodname.toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("foodname", foodname2);
                    fragmentFoodListItem.setArguments(bundle);
                    loadFragment(fragmentFoodListItem);
                }
            });

        }
        return view;

    }


    public void loadFragment(Fragment fragment){

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);

        transaction.commit();


    }

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }



}