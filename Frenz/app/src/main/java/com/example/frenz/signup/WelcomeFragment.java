package com.example.frenz.signup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frenz.HomeScreen;
import com.example.frenz.R;
import com.example.frenz.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WelcomeFragment extends Fragment {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Users");

    public WelcomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WelcomeFragment newInstance(String param1, String param2) {

        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authStateListener = firebaseAuth -> currentUser = firebaseAuth.getCurrentUser();
        User user = new User();
        SignUpViewModel signUpViewModel = ((SignUpActivity) requireActivity()).getSignUpViewModel();

        // Add logging to check values
        Log.d("appcheck", "User Email: " + signUpViewModel.getUserEmail());
        Log.d("appcheck", "User Password: " + signUpViewModel.getPassword());

        firebaseAuth.createUserWithEmailAndPassword(signUpViewModel.getUserEmail(), signUpViewModel.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            user.setUserID(currentUser.getUid());

                            Map<String, Object> userObj = new HashMap<>();
                            userObj.put("userID", currentUser.getUid());
                            userObj.put("username", signUpViewModel.getUsername());
                            userObj.put("userEmail", signUpViewModel.getUserEmail());
                            userObj.put("userFirstName", signUpViewModel.getFirstName());
                            userObj.put("userLastName", signUpViewModel.getLastName());

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
                            reference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        reference.child(currentUser.getUid()).updateChildren(userObj);
                                    } else {
                                        reference.child(currentUser.getUid()).setValue(userObj);
                                    }
                                    startActivity(new Intent(getActivity(), HomeScreen.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseAuth != null){
            currentUser = firebaseAuth.getCurrentUser();
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }
}