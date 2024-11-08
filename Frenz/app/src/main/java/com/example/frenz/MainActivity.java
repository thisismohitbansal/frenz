package com.example.frenz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frenz.login.LoginActivity;
import com.example.frenz.signup.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ViewFlipper viewFlipper;
    private static final int FLIP_INTERVAL = 2000;
    private Button loginBtn, signUpBtn;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewFlipper.showNext();
            handler.postDelayed(this, FLIP_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeScreen.class));
            finish();
        }

        viewFlipper = findViewById(R.id.viewFlipper);
        loginBtn = findViewById(R.id.login_btn_main_activity);
        signUpBtn = findViewById(R.id.sign_up_btn_main_activity);
        handler.postDelayed(runnable, FLIP_INTERVAL);

        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_btn_main_activity){
            //Login Kro
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (view.getId() == R.id.sign_up_btn_main_activity) {
            //SignUp kro
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        }
    }
}
