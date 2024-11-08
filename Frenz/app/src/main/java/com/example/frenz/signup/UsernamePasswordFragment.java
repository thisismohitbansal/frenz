package com.example.frenz.signup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.frenz.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.atomic.AtomicBoolean;

public class UsernamePasswordFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextInputEditText usernameView;
    private TextInputEditText passwordView;
    private CardView confirmDetailBtn;
    private SignUpViewModel signUpViewModel;

    private String mParam1;
    private String mParam2;

    public UsernamePasswordFragment() {
        // Required empty public constructor
    }

    public static UsernamePasswordFragment newInstance(String param1, String param2) {
        UsernamePasswordFragment fragment = new UsernamePasswordFragment();
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
        signUpViewModel = ((SignUpActivity) requireActivity()).getSignUpViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username_password, container, false);
        usernameView = view.findViewById(R.id.username_input_edit_text);
        passwordView = view.findViewById(R.id.password_input_edit_text);
        confirmDetailBtn = view.findViewById(R.id.fragment_username_password_confirm_details_btn);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Button click listener
        confirmDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click, e.g., navigate to the next fragment
                String username = usernameView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();
                if (getActivity() != null && getActivity() instanceof SignUpActivity
                        && password.length()>6) {
                    checkUsernameExists(username,password, db);
                }
                else {
                    Toast.makeText(getContext(), "Password must be 6 digits", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void checkUsernameExists(String username, String password, FirebaseFirestore db) {
        // Check if the username already exists in the Firestore collection
        db.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .continueWith(new Continuation<QuerySnapshot, Void>() {
                    @Override
                    public Void then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Username already exists
                                Toast.makeText(getContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                // Username is unique, proceed to the next fragment
                                signUpViewModel.setUsername(username);
                                signUpViewModel.setPassword(password);
                                ((SignUpActivity) getActivity()).navigateToNextFragment(
                                        WelcomeFragment.newInstance("", "")
                                );
                            }
                        } else {
                            Log.d("appcheck", "Error checking username existence", task.getException());
                        }
                        return null;
                    }
                });
    }

}
