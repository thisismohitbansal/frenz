package com.example.frenz.homeActivity.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.frenz.HomeScreen;
import com.example.frenz.R;
import com.example.frenz.homeActivity.filemodel.commentviewholder;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditFragment extends Fragment {
    private ImageView cameraBtn;
    private ShapeableImageView imagePreview;
    private EditText profileDesc;
    private CardView doneBtn;
    private ProgressBar progressBar;
    private static final int IMAGE_PICKER_REQUEST_CODE = 123;
    private String userID, userProfileImage;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance() {
        return new EditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        imagePreview = view.findViewById(R.id.image_preview_edit);
        cameraBtn = view.findViewById(R.id.camera_btn);
        profileDesc = view.findViewById(R.id.profile_desc);
        progressBar = view.findViewById(R.id.imageProgress);
        doneBtn = view.findViewById(R.id.fragment_edit_profile_confirm_details_btn);

        progressBar.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(getContext());
        reference = FirebaseDatabase.getInstance().getReference().child("user");
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(EditFragment.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start(IMAGE_PICKER_REQUEST_CODE);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(userID, profileDesc.getText().toString(), userProfileImage);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            userProfileImage = uri.toString();
            Log.d("appcheck", "onActivityResult: " + uri.toString());
            imagePreview.setImageURI(uri);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void updateProfile(String userID, String userDescption, String profileImage) {
        final Map<String, Object> map = new HashMap<>();
        progressDialog.show();

        // Check if user has updated profile image
        if (profileImage != null && !profileImage.isEmpty()) {
            final StorageReference uploader = storageReference.child("profile_images/" + "img" + System.currentTimeMillis());
            uploader.putFile(Uri.parse(profileImage))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    map.put("user_profile_img", uri.toString());
                                    map.put("user_descption", userDescption);
                                    updateUserProfile(map, userID);
                                }
                            });
                        }
                    });
        } else {
            // User has not updated profile image
            map.put("user_descption", userDescption);
            updateUserProfile(map, userID);
        }
    }

    // Helper method to update user profile in Firebase
    private void updateUserProfile(Map<String, Object> map, String userID) {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference.child(userID).updateChildren(map);
                } else {
                    reference.child(userID).setValue(map);
                }
                progressDialog.dismiss();
                startActivity(new Intent(getActivity(), HomeScreen.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToProfile(){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Log.d("appcheck", "starting edit: ");
        ProfileFragment profileFragment = new ProfileFragment();
        fragmentTransaction.replace(R.id.fragment_container_home_screen, profileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        getUserProfileImage(userID);
        DatabaseReference dbrefer = FirebaseDatabase.getInstance().getReference().child("user").child(userID);
        dbrefer.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String userDescription = snapshot.child("user_descption").getValue(String.class);;
                    if (!userDescription.isEmpty()) {
                        profileDesc.setText(userDescription);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void getUserProfileImage(String userID){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userProfileImage = dataSnapshot.child("user_profile_img").getValue(String.class);
                    Picasso.get().load(userProfileImage).into(imagePreview);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

}
