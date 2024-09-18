package com.example.intaketraackerapp.ui;

import android.content.Intent;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.example.intaketraackerapp.ui.calendar.FragmentCalendar;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class FragmentInformationDay extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentFoodListItem fragmentFoodListItem = new FragmentFoodListItem();

    FragmentCalendar fragmentCalendar = new FragmentCalendar();


    // TODO: Rename and change types of parameters

    private int year;
    private int month;
    private int day;

    TextView calories;
    TextView proteins;
    TextView carbs;
    TextView fats;
    TextView date;
    Button button;

    private ListView listView;


    public FragmentInformationDay() {
        // Required empty public constructor
    }

    public static FragmentInformationDay newInstance(String param1, String param2) {
        FragmentInformationDay fragment = new FragmentInformationDay();
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
            this.day = getArguments().getInt("day");
            this.month = getArguments().getInt("month");
            this.year = getArguments().getInt("year");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_information_day, container, false);

        calories = (TextView) view.findViewById(R.id.Calories);
        proteins = (TextView) view.findViewById(R.id.Proteins);
        carbs = (TextView) view.findViewById(R.id.Carbs);
        fats = (TextView) view.findViewById(R.id.Fats);
        date = (TextView) view.findViewById(R.id.Selecteddate);
        button = (Button) view.findViewById(R.id.buttonSelectAnotherDate);
        listView = (ListView) view.findViewById(R.id.food_list);

        //Leer Json
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
        User user = gson.fromJson(contenidoJSON, User.class);

        //MOSTRAR FECHA
        /*Calendar calendar = Calendar.getInstance();
        month = month - 1;
        calendar.set(year, month, day);*/
        String monthString;
        month = (month+1);
        if (month < 10) {
            monthString = "0" + month; // se agrega un cero delante del mes si es menor a 10
        } else {
            monthString = String.valueOf(month);
        }
        String dayOfMonth;
        if (day < 10) {
            dayOfMonth = "0" + day;
        }
        else {
            dayOfMonth = String.valueOf(day);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = year + "-" + monthString + "-" + dayOfMonth;
        //Date data = dateFormat.parse(dateString);
        //Date data = calendar.getTime();

        date.setText(dateString);

        float cal = 0, pro = 0, car = 0, fat = 0;
        ArrayList<Food> foodList = user.getConsumedFood();
        ArrayList<String> foodListNames = new ArrayList<String>();

        if (foodList != null) {
            for (int i = 0; i < foodList.size(); i++) {
                Food food = foodList.get(i);
                if(food.getDateConsumed().equals(dateString)){
                    foodListNames.add(food.getName());
                    cal = food.getCalories() + cal;
                    pro = food.getProteins() + pro;
                    car = food.getCarbs() + car;
                    fat = food.getFats() + fat;
                }
            }

            DecimalFormat df = new DecimalFormat("#.##");
            calories.setText(String.valueOf(df.format(cal)) + " kcal");
            proteins.setText(String.valueOf(df.format(pro))+ " g");
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}