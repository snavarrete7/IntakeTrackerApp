package com.example.intaketraackerapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.JsonFunctions;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentFoodListItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFoodListItem extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String foodname;
    TextView foodTitle;
    TextView calories;
    TextView proteins;
    TextView carbs;
    TextView fats;

    public FragmentFoodListItem() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFoodListItem.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFoodListItem newInstance(String param1, String param2) {
        FragmentFoodListItem fragment = new FragmentFoodListItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.foodname = (String) this.getArguments().getString("foodname");
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

        View view = inflater.inflate(R.layout.fragment_food_list_item, container, false);

        foodTitle = (TextView) view.findViewById(R.id.foodnameTitle);
        calories = (TextView) view.findViewById(R.id.textCalories);
        proteins = (TextView) view.findViewById(R.id.textProteins);
        carbs = (TextView) view.findViewById(R.id.textCarbs);
        fats = (TextView) view.findViewById(R.id.textFats);

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

        for(int i =  0; i< user.getConsumedFood().size(); i++){
            Food food = user.getConsumedFood().get(i);
            String name = food.getName();
            if(name.equals(this.foodname)){

                char ultimoCaracter = name.charAt(name.length() - 1);
                boolean esNumero = Character.isDigit(ultimoCaracter);

                if(esNumero){
                    String newname = name.substring(0, name.length() - 3);
                    int cantidad = Character.getNumericValue(ultimoCaracter);
                    foodTitle.setText(newname);
                    calories.setText(String.valueOf(food.getCalories() / cantidad) + " kcal");
                    proteins.setText(String.valueOf(food.getProteins() / cantidad) + " g");
                    fats.setText(String.valueOf(food.getFats() / cantidad) + " g");
                    carbs.setText(String.valueOf(food.getCarbs() / cantidad) + " g");

                }else {
                    foodTitle.setText(name);
                    calories.setText(String.valueOf(food.getCalories()) + " kcal");
                    proteins.setText(String.valueOf(food.getProteins()) + " g");
                    fats.setText(String.valueOf(food.getFats()) + " g");
                    carbs.setText(String.valueOf(food.getCarbs()) + " g");

                }
                break;
            }
        }




        return view;
    }
}