package com.example.frenz.homeActivity.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.frenz.MainActivity;
import com.example.frenz.R;
import com.example.frenz.homeActivity.filemodel.commentviewholder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private TextView usernameTextview, userDesc;
    private ShapeableImageView profileImg;
    private CardView logout_view;
    private CardView edit_profile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logout_view = view.findViewById(R.id.logout_btn);
        edit_profile = view.findViewById(R.id.edit_profile_btn);
        userDesc = view.findViewById(R.id.userDesciption_profile);
        usernameTextview = view.findViewById(R.id.username_profile);
        profileImg = view.findViewById(R.id.profile_img_profile);
        logout_view.setOnClickListener(this);
        edit_profile.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.logout_btn){
            logout();
        }
        else if(view.getId() == R.id.edit_profile_btn){
            editProfile();
            Log.d("appcheck", "onClick: edit clicked");
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void editProfile(){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Log.d("appcheck", "starting edit: ");
        EditFragment editProfileFragment = new EditFragment();
        fragmentTransaction.replace(R.id.fragment_container_home_screen, editProfileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        getUserProfileImage(userID);
    }

    private void getUserProfileImage(String userID){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userProfileImage = dataSnapshot.child("user_profile_img").getValue(String.class);
                    Picasso.get().load(userProfileImage).into(profileImg);
                    String userDescription = dataSnapshot.child("user_descption").getValue(String.class);
                    userDesc.setText(userDescription);
                    String userName = dataSnapshot.child("username").getValue(String.class);
                    usernameTextview.setText(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }
}