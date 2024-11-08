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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmailFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextInputEditText emailAddress;
    private CardView confirmDetailBtn;
    private SignUpViewModel signUpViewModel;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public EmailFragment() {
        // Required empty public constructor
    }

    public static EmailFragment newInstance(String param1, String param2) {
        EmailFragment fragment = new EmailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signUpViewModel = ((SignUpActivity) requireActivity()).getSignUpViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);

        confirmDetailBtn = view.findViewById(R.id.fragment_email_confirm_details_btn);
        emailAddress = view.findViewById(R.id.email_input_edit_text);

        // Button click listener

        confirmDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click, e.g., navigate to the next fragment

                String email = Objects.requireNonNull(emailAddress.getText()).toString();
                if (getActivity() != null && getActivity() instanceof SignUpActivity
                        && email.contains("@")) {
                    checkEmailExists(email,firebaseAuth,db);
                }
                else{
                    Toast.makeText(getContext(), "Invalid Email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Assuming you want to perform some action when the user types in the email field
        emailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Call the method when email is entered
                    confirmDetailBtn.setBackgroundColor(getResources().getColor(R.color.confirm_enable));
                }
                else{
                    confirmDetailBtn.setBackgroundColor(getResources().getColor(R.color.confirm_disable));
                }
            }
        });

        return view;
    }

    private void checkEmailExists(String email, FirebaseAuth firebaseAuth, FirebaseFirestore db) {
        // Check if the email already exists in the Firestore collection
        db.collection("Users")
                .whereEqualTo("userEmail", email)
                .get()
                .continueWith(new Continuation<QuerySnapshot, Void>() {
                    @Override
                    public Void then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Email already exists
                                Toast.makeText(getContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                // Email is unique
                                signUpViewModel.setUserEmail(email);
                                ((SignUpActivity) getActivity()).navigateToNextFragment(NameFragment.newInstance("", ""));
                            }
                        } else {
                            Log.d("appcheck", "Error checking email existence", task.getException());
                        }
                        return null;
                    }
                });
    }


}
