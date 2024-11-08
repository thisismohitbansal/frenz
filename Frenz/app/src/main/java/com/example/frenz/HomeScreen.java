package com.example.frenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.frenz.homeActivity.fragment.HomeFragment;
import com.example.frenz.homeActivity.fragment.ProfileFragment;
import com.example.frenz.homeActivity.fragment.SearchFragment;
import com.example.frenz.homeActivity.fragment.WriteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home_screen, new HomeFragment()).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.action_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.action_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.action_add_post) {
                selectedFragment = new WriteFragment();
            } else if (itemId == R.id.action_profile) {
                selectedFragment = new ProfileFragment();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home_screen, selectedFragment).commit();
            return true;
        });
    }
}