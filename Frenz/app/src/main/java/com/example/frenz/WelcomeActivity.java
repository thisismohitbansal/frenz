package com.example.frenz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent to start the new activity
                Intent intent = new Intent(WelcomeActivity.this, HomeScreen.class);
                startActivity(intent);
                finish(); // Finish the current activity
            }
        }, 2000);
    }
}