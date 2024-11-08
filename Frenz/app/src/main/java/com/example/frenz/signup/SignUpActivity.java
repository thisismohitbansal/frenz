// SignUpActivity.java

package com.example.frenz.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.frenz.R;
import com.example.frenz.model.User;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;
    public static final String tag = "appcheck";
    private String userEmail;
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        if (savedInstanceState == null) {
            // Load the initial fragment (EmailFragment)
            loadFragment(new EmailFragment());
        }
    }

    public SignUpViewModel getSignUpViewModel() {
        return signUpViewModel;
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    // Method to navigate to the next fragment
    public void navigateToNextFragment(Fragment nextFragment) {
        loadFragment(nextFragment);
    }

        // Method to navigate to WelcomeFragment with collected data

    public void navigateToWelcomeFragment() {
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        Log.d("appcheck", "User Email: " + userEmail);
        Log.d("appcheck", "First Name: " + firstName);
        Log.d("appcheck", "Last Name: " + lastName);
        Log.d("appcheck", "Username: " + username);
        Log.d("appcheck", "Password: " + password);
        loadFragment(welcomeFragment);
    }
    // You can define additional methods for communication between fragments
}
