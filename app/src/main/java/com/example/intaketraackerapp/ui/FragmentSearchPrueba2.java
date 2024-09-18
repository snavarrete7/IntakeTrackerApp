package com.example.intaketraackerapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.FoodList;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearchPrueba2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearchPrueba2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView listaSearch;
    FragmentFoodSearched fragmentFoodSearched = new FragmentFoodSearched();

    public FragmentSearchPrueba2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearchPrueba2.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearchPrueba2 newInstance(String param1, String param2) {
        FragmentSearchPrueba2 fragment = new FragmentSearchPrueba2();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_prueba2, container, false);

        listaSearch = view.findViewById(R.id.listfoodSearch);
        Gson gson = new Gson();
        String contenidoJSON;
        try {
            InputStreamReader archivo = new InputStreamReader(getActivity().openFileInput("foods.json"));
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
        FoodList listFoodSearch = gson.fromJson(contenidoJSON, FoodList.class);

        ArrayList<String> foodlistnames = new ArrayList<String>();
        for(int i=0; i < listFoodSearch.getLista().size(); i++){
           foodlistnames.add(listFoodSearch.getLista().get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, foodlistnames);
        listaSearch.setAdapter(adapter);

        listaSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object foodname = listaSearch.getItemAtPosition(i);
                String foodname2 = foodname.toString();

                Food food = null;
                for (int j = 0; j < listFoodSearch.getLista().size(); j++) {
                    if (listFoodSearch.getLista().get(j).getName().equals(foodname2)) {
                        String name = listFoodSearch.getLista().get(j).getName();
                        float calories = listFoodSearch.getLista().get(j).getCalories();
                        float carbs = listFoodSearch.getLista().get(j).getCarbs();
                        float fats = listFoodSearch.getLista().get(j).getFats();
                        float pro = listFoodSearch.getLista().get(j).getProteins();
                        food = new Food(name, calories, fats, carbs, pro, null);

                    }
                }
                Bundle bundle = new Bundle();
                bundle.putString("foodClickedName", food.getName());
                bundle.putFloat("foodClickedPro", food.getProteins());
                bundle.putFloat("foodClickedFats", food.getFats());
                bundle.putFloat("foodClickedCal", food.getCalories());
                bundle.putFloat("foodClickedCarbs", food.getCarbs());
                fragmentFoodSearched.setArguments(bundle);
                loadFragment(fragmentFoodSearched);
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