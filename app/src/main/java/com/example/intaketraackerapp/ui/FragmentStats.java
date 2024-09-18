package com.example.intaketraackerapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
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
import com.example.intaketraackerapp.ui.calendar.FragmentCalendar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentStats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStats extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    BottomNavigationView navView;

    TextView totalCal;
    TextView aCal;
    TextView totalPro;
    TextView aPro;
    TextView totalFats;
    TextView aFats;
    TextView totalCarbs;
    TextView aCarbs;

    Button lastDay;
    Button lastWeek;
    Button lastMonth;
    Button lastYear;
    Button seeForDay;




    public FragmentStats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentStats.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentStats newInstance(String param1, String param2) {
        FragmentStats fragment = new FragmentStats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        navView.getMenu().findItem(R.id.navigation_statistics).setChecked(true);
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

        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        totalCal = (TextView) view.findViewById(R.id.textView_totalCal);
        aCal = (TextView) view.findViewById(R.id.textView_aCal);
        totalPro = (TextView) view.findViewById(R.id.textView_totalPro);
        aPro = (TextView) view.findViewById(R.id.textView_aPro);
        totalFats = (TextView) view.findViewById(R.id.textView_totalFats);
        aFats = (TextView) view.findViewById(R.id.textView_aFats);
        totalCarbs = (TextView) view.findViewById(R.id.textView_totalCarb);
        aCarbs = (TextView) view.findViewById(R.id.textView_aCarb);
        lastDay = (Button) view.findViewById(R.id.button_last_day);
        lastWeek = (Button) view.findViewById(R.id.button_last_week);
        lastMonth = (Button) view.findViewById(R.id.button_last_month);
        lastYear = (Button) view.findViewById(R.id.button_last_year);
        seeForDay = (Button) view.findViewById(R.id.button_calendar);

        lastDay.setBackgroundColor(Color.YELLOW);
        lastWeek.setBackgroundColor(Color.rgb(255, 165, 0));
        lastMonth.setBackgroundColor(Color.rgb(255, 165, 0));
        lastYear.setBackgroundColor(Color.rgb(255, 165, 0));


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

        JsonFunctions jsonFunctions = new JsonFunctions();
        User user = jsonFunctions.readJson(getActivity());

        ArrayList<Food> listFood = user.getConsumedFood();

        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        LocalDate finalCurrentDate = currentDate;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastDay.performClick();
            }
        }, 0);

        lastDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lastDay.setBackgroundColor(Color.YELLOW);
                lastWeek.setBackgroundColor(Color.rgb(255, 165, 0));
                lastMonth.setBackgroundColor(Color.rgb(255, 165, 0));
                lastYear.setBackgroundColor(Color.rgb(255, 165, 0));

                float cal = 0, pro = 0, carbs = 0, fats = 0;

                LocalDate datedays = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                   datedays = finalCurrentDate.minusDays(1);
                }

                String date_lastDay = datedays.toString();

                for(int i =0; i < listFood.size(); i++){

                    Food food = listFood.get(i);


                    if(food.getDateConsumed().equals(date_lastDay)){
                        cal = cal + food.getCalories();
                        pro = pro + food.getProteins();
                        carbs = carbs + food.getCarbs();
                        fats = fats + food.getFats();
                    }

                }
                DecimalFormat df = new DecimalFormat("#.##");
                totalCal.setText(String.valueOf(df.format(cal)));
                aCal.setText(String.format("%.2f", (cal)));
                totalPro.setText(String.valueOf(df.format(pro)));
                aPro.setText(String.format("%.2f", (pro)));
                totalCarbs.setText(String.valueOf(df.format(carbs)));
                aCarbs.setText(String.format("%.2f", (carbs)));
                totalFats.setText(String.valueOf(df.format(fats)));
                aFats.setText(String.format("%.2f", (fats)));
            }
        });

        lastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lastWeek.setBackgroundColor(Color.YELLOW);
                lastDay.setBackgroundColor(Color.rgb(255, 165, 0));
                lastMonth.setBackgroundColor(Color.rgb(255, 165, 0));
                lastYear.setBackgroundColor(Color.rgb(255, 165, 0));

                float cal = 0, pro = 0, carbs = 0, fats = 0;
                LocalDate dateweek = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dateweek = finalCurrentDate.minusDays(7);
                }

                for(int i=0; i<listFood.size();i++){

                    Food food = listFood.get(i);
                    LocalDate dateConsumedFood = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dateConsumedFood = LocalDate.parse(food.getDateConsumed());
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if(dateweek.compareTo(dateConsumedFood) <= 0){
                            if(finalCurrentDate.compareTo(dateConsumedFood) != 0) {
                                cal = cal + food.getCalories();
                                pro = pro + food.getProteins();
                                carbs = carbs + food.getCarbs();
                                fats = fats + food.getFats();
                            }
                        }
                    }

                }

                DecimalFormat df = new DecimalFormat("#.##");
                totalCal.setText(String.valueOf(df.format(cal)));
                aCal.setText(String.format("%.2f", (cal/7.0)));
                totalPro.setText(String.valueOf(df.format(pro)));
                aPro.setText(String.format("%.2f", (pro/7.0)));
                totalCarbs.setText(String.valueOf(df.format(carbs)));
                aCarbs.setText(String.format("%.2f", (carbs/7.0)));
                totalFats.setText(String.valueOf(df.format(fats)));
                aFats.setText(String.format("%.2f", (fats/7.0)));

            }
        });

        lastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lastMonth.setBackgroundColor(Color.YELLOW);
                lastWeek.setBackgroundColor(Color.rgb(255, 165, 0));
                lastDay.setBackgroundColor(Color.rgb(255, 165, 0));
                lastYear.setBackgroundColor(Color.rgb(255, 165, 0));

                float cal = 0, pro = 0, carbs = 0, fats = 0;
                LocalDate datemonth = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    datemonth = finalCurrentDate.minusDays(30);
                }

                for(int i=0; i<listFood.size();i++){

                    Food food = listFood.get(i);
                    LocalDate dateConsumedFood = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dateConsumedFood = LocalDate.parse(food.getDateConsumed());
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if(datemonth.compareTo(dateConsumedFood) <= 0){
                            if(finalCurrentDate.compareTo(dateConsumedFood) != 0) {
                                cal = cal + food.getCalories();
                                pro = pro + food.getProteins();
                                carbs = carbs + food.getCarbs();
                                fats = fats + food.getFats();
                            }
                        }
                    }

                }
                DecimalFormat df = new DecimalFormat("#.##");
                totalCal.setText(String.valueOf(df.format(cal)));
                aCal.setText(String.format("%.2f", (cal/30.0)));
                totalPro.setText(String.valueOf(df.format(pro)));
                aPro.setText(String.format("%.2f", (pro/30.0)));
                totalCarbs.setText(String.valueOf(df.format(carbs)));
                aCarbs.setText(String.format("%.2f", (carbs/30.0)));
                totalFats.setText(String.valueOf(df.format(fats)));
                aFats.setText(String.format("%.2f", (fats/30.0)));
            }
        });

        lastYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lastYear.setBackgroundColor(Color.YELLOW);
                lastWeek.setBackgroundColor(Color.rgb(255, 165, 0));
                lastDay.setBackgroundColor(Color.rgb(255, 165, 0));
                lastMonth.setBackgroundColor(Color.rgb(255, 165, 0));

                float cal = 0, pro = 0, carbs = 0, fats = 0;
                LocalDate dateyear = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dateyear = finalCurrentDate.minusDays(365);
                }

                for(int i=0; i<listFood.size();i++){

                    Food food = listFood.get(i);
                    LocalDate dateConsumedFood = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dateConsumedFood = LocalDate.parse(food.getDateConsumed());
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if(dateyear.compareTo(dateConsumedFood) <= 0){
                            if(finalCurrentDate.compareTo(dateConsumedFood) != 0) {
                                cal = cal + food.getCalories();
                                pro = pro + food.getProteins();
                                carbs = carbs + food.getCarbs();
                                fats = fats + food.getFats();
                            }
                        }
                    }

                }

                DecimalFormat df = new DecimalFormat("#.##");
                totalCal.setText(String.valueOf(df.format(cal)));
                aCal.setText(String.format("%.2f", (cal/365.0)));
                totalPro.setText(String.valueOf(df.format(pro)));
                aPro.setText(String.format("%.2f", (pro/365.0)));
                totalCarbs.setText(String.valueOf(df.format(carbs)));
                aCarbs.setText(String.format("%.2f", (carbs/365.0)));
                totalFats.setText(String.valueOf(df.format(fats)));
                aFats.setText(String.format("%.2f", (fats/365.0)));
            }
        });
        seeForDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentCalendar fragmentCalendar = new FragmentCalendar();
                loadFragment(fragmentCalendar);
            }
        });

        return view;
    }

    public void loadFragment(Fragment fragment){

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);

        transaction.commit();
    }
}