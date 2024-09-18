package com.example.intaketraackerapp.ui;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.FoodList;
import com.example.intaketraackerapp.IntakeClasses.JsonFunctions;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.intaketraackerapp.IntakeClasses.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearchPRUEBA#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearchPRUEBA extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SearchView searchView;
    ListView listView;
    FragmentFoodSearched fragmentFoodSearched = new FragmentFoodSearched();

    BottomNavigationView navView;
    boolean searchDone = false;
    private ConstraintLayout searchFood;
    private FrameLayout chargingIcon;
    public FragmentSearchPRUEBA() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearchPRUEBA newInstance(String param1, String param2) {
        FragmentSearchPRUEBA fragment = new FragmentSearchPRUEBA();
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
        navView.getMenu().findItem(R.id.navigation_search).setChecked(true);
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
        View view= inflater.inflate(R.layout.fragment_search_prueba, container, false);
        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.ListViewID);
        chargingIcon = view.findViewById(R.id.loadingContainer);
        searchFood = view.findViewById(R.id.search_food);

        JsonFunctions jsonFunctions = new JsonFunctions();
        User user = jsonFunctions.readJson(getActivity());

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

        Type listType2 = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        Gson gsonresearch = new Gson();
        ArrayList<ArrayList<String>> arrayList = gsonresearch.fromJson(resSearchedFood[0], listType2);

        ArrayList<String> foodlistnames = new ArrayList<String>();
        for(int i=0; i < arrayList.size(); i++){

            String namefood = arrayList.get(i).get(1);
            StringBuilder stringBuilder = new StringBuilder();

            if (hasConsecutivePercent20(namefood)){
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

            foodlistnames.add(namefood);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, foodlistnames);
        listView.setAdapter(adapter);

        /////////////////////////////////////////////////////


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showLoadingIcon();
                try {
                    Object foodname = listView.getItemAtPosition(i);
                    String foodname2 = foodname.toString();

                    String[] res = {""};
                    final boolean[] searched = {false};
                    String url = Utils.serverUri + "/searchFoodInformation?query=" + foodname2;
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
                            hideLoadingIcon();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                if (response.isSuccessful()) {
                                    res[0] = response.body().string();
                                    System.out.println(res[0]);
                                    Type listType = new TypeToken<ArrayList<String>>() {
                                    }.getType();
                                    Gson gsonfood = new Gson();
                                    ArrayList<String> foodSearched = gsonfood.fromJson(res[0], listType);

                                    Bundle bundle = new Bundle();

                                    bundle.putString("foodClickedName", foodname2);
                                    bundle.putFloat("foodClickedPro", Float.parseFloat(foodSearched.get(0)));
                                    bundle.putFloat("foodClickedFats", Float.parseFloat(foodSearched.get(1)));
                                    bundle.putFloat("foodClickedCal", Float.parseFloat(foodSearched.get(3)));
                                    bundle.putFloat("foodClickedCarbs", Float.parseFloat(foodSearched.get(2)));
                                    fragmentFoodSearched.setArguments(bundle);
                                    searchDone = true;
                                    loadFragment(fragmentFoodSearched);
                                }
                            } finally {
                                response.close();
                                hideLoadingIcon();
                            }
                        }
                    });

                   /*
                    Object foodname = listView.getItemAtPosition(i);
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
                    loadFragment(fragmentFoodSearched);*/
                } catch (Exception e){
                    hideLoadingIcon();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                showLoadingIcon();
                String[] res = {""};
                final boolean[] searched = {false};
                String url = Utils.serverUri + "/searchFoodInformation?query="+ query;
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
                        Utils.showToast("Could not connect with the server, please try again later", getActivity());
                        hideLoadingIcon();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if(response.isSuccessful()) {
                                res[0] = response.body().string();

                                String result = res[0].trim();
                                if(!Objects.equals(result, "Not Found")){
                                    Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                                    Gson gsonfood = new Gson();
                                    ArrayList<String> foodSearched = gsonfood.fromJson(res[0], listType);

                                    Bundle bundle = new Bundle();

                                    bundle.putString("foodClickedName", query);
                                    bundle.putFloat("foodClickedPro", Float.parseFloat(foodSearched.get(0)));
                                    bundle.putFloat("foodClickedFats", Float.parseFloat(foodSearched.get(1)));
                                    bundle.putFloat("foodClickedCal", Float.parseFloat(foodSearched.get(3)));
                                    bundle.putFloat("foodClickedCarbs", Float.parseFloat(foodSearched.get(2)));
                                    fragmentFoodSearched.setArguments(bundle);
                                    searchDone = true;
                                    response.close();
                                    loadFragment(fragmentFoodSearched);
                                }
                                else {
                                    Utils.showToast("Please enter a correct food name", getActivity());
                                }
                            }
                        } catch (Exception e){
                            response.close();
                        }
                        hideLoadingIcon();
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                /*
                adapter.getFilter().filter(newText);
                if (newText.equals("")) {
                    listView.setVisibility(View.GONE);
                }else{
                    listView.setVisibility(View.VISIBLE);
                }*/
                return false;
            }
        });
        /*if(searchDone == true) {
            loadFragment(fragmentFoodSearched);
        }*/
        return view;
    }

    public void loadFragment(Fragment fragment){

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);

        transaction.commit();
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
    private void showLoadingIcon() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        chargingIcon.setVisibility(View.VISIBLE);
                        searchFood.setAlpha(0.5F);
                        chargingIcon.setAlpha(1F);
                    } catch (Exception e) {
                        Utils.showToast(e.getMessage(), getActivity());
                    }
                }
            });
        } catch (Exception e2) {
            Utils.showToast(e2.getMessage(), getActivity());
        }
    }

    private void hideLoadingIcon() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        searchFood.setAlpha(1F);
                        chargingIcon.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Utils.showToast(e.getMessage(), getActivity());
                    }
                }
            });
        } catch (Exception e2) {
            Utils.showToast(e2.getMessage(), getActivity());
        }
    }
}