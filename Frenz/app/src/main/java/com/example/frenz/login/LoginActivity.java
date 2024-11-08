package com.example.frenz.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.frenz.HomeScreen;
import com.example.frenz.R;
import com.example.frenz.WelcomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailText, passwordText;
    private CardView confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.login_page_username_input_edit_text);
        passwordText = findViewById(R.id.login_page_password_input_edit_text);
        confirmBtn = findViewById(R.id.login_activity_username_password_confirm_details_btn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    login(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Empty Fields not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d("appcheck", "login: successfull ");
                    startHomeScreen();
                })
                .addOnFailureListener(e -> {
                    Log.d("appcheck", e.toString());
                    handleLoginFailure(e.getMessage());
                });
    }

    private void startHomeScreen() {
        startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
        finish();
    }

    private void handleLoginFailure(String errorMessage) {
        if (errorMessage.contains("no user record")) {
            Toast.makeText(this, "Email does not exist", Toast.LENGTH_SHORT).show();
        } else if (errorMessage.contains("password is invalid")) {
            Toast.makeText(this, "Invalid Email/Password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid Email/Password", Toast.LENGTH_SHORT).show();
        }
    }
}
