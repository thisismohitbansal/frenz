package com.example.frenz.homeActivity.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenz.HomeScreen;
import com.example.frenz.R;
import com.example.frenz.homeActivity.filemodel.FileModel;
import com.example.frenz.homeActivity.filemodel.commentviewholder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

public class WriteFragment extends Fragment implements View.OnClickListener{
    private TextView usernameTextView;
    private ShapeableImageView profileImg;
    private ProgressBar progressBar;
    private ImageView addImage, imageHolder;
    private CardView postBtn;
    private EditText postEditText;
    private Uri imageURI;
    private String imageurl;
    private String USERNAME;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    public WriteFragment() {
        // Required empty public constructor
    }
    public static WriteFragment newInstance(String param1, String param2) {
        return new WriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("myimages");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);
        addImage = view.findViewById(R.id.add_image_btn);
        usernameTextView = view.findViewById(R.id.profile_name_new_post);
        profileImg = view.findViewById(R.id.profile_picture_new_post);
        imageHolder = view.findViewById(R.id.image_place_holder);
        postBtn = view.findViewById(R.id.fragment_write_confirm_details_btn);
        postEditText = view.findViewById(R.id.post_text);
        progressBar = view.findViewById(R.id.writeProgress);

        addImage.setOnClickListener(this);
        postBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add_image_btn){
            addImage();
        }
        else if(view.getId() == R.id.fragment_write_confirm_details_btn){
            uploadPost();
        }
    }

    private void addImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (data != null) {
                imageURI = data.getData();
                Picasso.get().load(imageURI).into(imageHolder);
                imageHolder.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getExtension() {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageURI));
    }


    private void uploadPost() {
        progressBar.setVisibility(View.VISIBLE);

        if (imageURI != null) {
            final StorageReference uploader = storageReference.child("myimages/" + System.currentTimeMillis() + "." + getExtension());
            uploader.putFile(imageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadPostWithImage(uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), "Failed to Upload Image!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Failed to Upload Image!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No image, only text
            uploadPostWithImage(null);
        }
    }

//    String postID, postDesc, Imageurl, userID;

    private void uploadPostWithImage(@Nullable String imageUrl) {
        FileModel obj;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        if (imageUrl != null) {
            obj = new FileModel(postEditText.getText().toString(), imageUrl);
        } else {
            obj = new FileModel(postEditText.getText().toString(), null);
        }

        String key = databaseReference.push().getKey();
        if (key != null) {
            obj.setPostID(key);
            obj.setUserID(userID);// Set the postID before uploading
//            obj.setTimeAdded(new Timestamp(new Date()));

            databaseReference.child(key).setValue(obj)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), HomeScreen.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Failed to Upload!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
                    String userName = dataSnapshot.child("username").getValue(String.class);
                    usernameTextView.setText(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }
}