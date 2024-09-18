package com.example.intaketraackerapp.ui.calendar;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.intaketraackerapp.HomePageActivity;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.intaketraackerapp.ui.FragmentHome;
import com.example.intaketraackerapp.ui.FragmentInformationDay;
import com.example.intaketraackerapp.R;

public class FragmentCalendar extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CalendarView calendarView;

    //private Spinner mSpinner;
    public FragmentCalendar() {
        // Required empty public constructor
    }

    public static FragmentCalendar newInstance(String param1, String param2) {
        FragmentCalendar fragment = new FragmentCalendar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);


        /*Gson gson = new Gson();
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

        //Creacion del Usuario a partir del JSON
        User user = gson.fromJson(contenidoJSON, User.class);

        ArrayList<Food> listFood = user.getConsumedFood();
        Long minDate;
        Long maxDate;*/

        calendarView = view.findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        calendarView.setMaxDate(cal.getTimeInMillis());

        /*minDate = calendarView.getMinDate();
        maxDate = calendarView.getMaxDate();


        mSpinner = findViewById(R.id.my_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.choose_circle));
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Manejar la selección del usuario aquí
                switch ((String) mSpinner.getSelectedItem()) {
                    case "Calories":
                        for (int i = 0; i < listFood.size(); i++) {
                            Food food = listFood.get(i);
                            for (long j = minDate; minDate <= maxDate; j++) {
                                if (food.getDateConsumed().equals(j)) {
                                    float calories = food.getCalories();
                                    float radioDay = (calories / 100);
                                    calendarView.setCircleRadius(radioDay);
                                }
                            }
                        }
                        break;
                    case "Proteins":
                        for (int i = 0; i < listFood.size(); i++) {
                            Food food = listFood.get(i);
                            for (long j = minDate; minDate <= maxDate; j++) {
                                if (food.getDateConsumed().equals(j)) {
                                    float proteins = food.getProteins();
                                    float radioDay = (proteins / 10);
                                    calendarView.setCircleRadius(radioDay);
                                }
                            }
                        }
                        break;
                    case "Fats":
                        for (int i = 0; i < listFood.size(); i++) {
                            Food food = listFood.get(i);
                            for (long j = minDate; minDate <= maxDate; j++) {
                                if (food.getDateConsumed().equals(j)) {
                                    float fats = food.getFats();
                                    float radioDay = (fats / 100);
                                    calendarView.setCircleRadius(radioDay);
                                }
                            }
                        }
                        break;
                    case "Carbs":
                        for (int i = 0; i < listFood.size(); i++) {
                            Food food = listFood.get(i);
                            for (long j = minDate; minDate <= maxDate; j++) {
                                if (food.getDateConsumed().equals(j)) {
                                    float carbs = food.getCarbs();
                                    float radioDay = (carbs / 10);
                                    calendarView.setCircleRadius(radioDay);
                                }
                            }
                        }
                        break;
                    case "Food":
                        for (int i = 0; i < listFood.size(); i++) {
                            Food food = listFood.get(i);
                            for (long j = minDate; minDate <= maxDate; j++) {
                                if (food.getDateConsumed().equals(j)) {
                                    String names = food.getName();
                                    String[] namesArray = names.split(",");
                                    int namesCount = namesArray.length;
                                    float radioDay = (namesCount / 10);
                                    calendarView.setCircleRadius(radioDay);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar la falta de selección del usuario aquí
            }
        });*/

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                FragmentInformationDay informationDayFragment = new FragmentInformationDay();
                Bundle args = new Bundle();
                args.putInt("year", year);
                args.putInt("month", month);
                args.putInt("day", dayOfMonth);
                informationDayFragment.setArguments(args);

                // inicia una transacción de fragmentos para agregar el fragmento al contenedor en CalendarActivity
                loadFragment(informationDayFragment);
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