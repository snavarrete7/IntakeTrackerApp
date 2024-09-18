package com.example.intaketraackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.ui.FragmentHome;
import com.example.intaketraackerapp.ui.FragmentProfile;
import com.example.intaketraackerapp.ui.FragmentSearchPRUEBA;
import com.example.intaketraackerapp.ui.FragmentSearchPrueba2;
import com.example.intaketraackerapp.ui.FragmentStats;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.intaketraackerapp.databinding.ActivityHomePageBinding;

import java.io.IOException;
import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    private ActivityHomePageBinding binding;
    private BottomNavigationView navigation;
    public Intent intent = getIntent();

    FragmentHome fragmentHome = new FragmentHome();
    FragmentStats fragmentStats = new FragmentStats();
    FragmentProfile fragmentProfile = new FragmentProfile();
    FragmentSearchPRUEBA fragmentSearchPRUEBA = new FragmentSearchPRUEBA();
    FragmentSearchPrueba2 fragmentSearchPrueba2 = new FragmentSearchPrueba2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener);
        loadFragment(fragmentHome);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener OnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item){

            switch (item.getItemId()){
                case R.id.navigation_home:
                    loadFragment(fragmentHome);
                    return true;
                case R.id.navigation_statistics:
                    loadFragment(fragmentStats);
                    return true;
                case R.id.navigation_profile:
                    loadFragment(fragmentProfile);
                    return true;
                case R.id.navigation_search:
                    loadFragment(fragmentSearchPRUEBA);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment){

        //Bundle bundle = getIntent().getExtras();
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        //fragment.setArguments(bundle);
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();

    }

}