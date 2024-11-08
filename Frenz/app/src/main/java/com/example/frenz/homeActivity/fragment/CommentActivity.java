package com.example.frenz.homeActivity.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenz.R;
import com.example.frenz.homeActivity.filemodel.CommentModel;
import com.example.frenz.homeActivity.filemodel.commentviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    ShapeableImageView profileImg, postImage;
    TextView usernameview;
    TextView userDesc;
    private EditText commentEditText;
    private Button submitBtn;
    private RecyclerView recyclerView;
    private DatabaseReference userref, commentref;
    private FirebaseRecyclerAdapter<CommentModel, commentviewholder> adapter;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentEditText = findViewById(R.id.commentText);
        submitBtn = findViewById(R.id.commentSubmit);
        recyclerView = findViewById(R.id.recyclerComment);
        profileImg = findViewById(R.id.comment_user_profile_image);
        postImage = findViewById(R.id.comment_image_to_be_shown);
        usernameview = findViewById(R.id.comment_user_profile_name);
        userDesc = findViewById(R.id.comment_post_descption_sigle_row);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is signed in, proceed with commenting
                    userID = currentUser.getUid();
                    // ... (your existing code)
                } else {
                    // User is not signed in, display a message or prompt for authentication
//                    Toast.makeText(CommentActivity.this, "Please sign in to comment", Toast.LENGTH_SHORT).show();
                    // You can redirect the user to the sign-in activity or provide options to sign in
                }
            }
        });

        String postID = getIntent().getStringExtra("postID");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getPostReady(postID, user.getUid());

        userref = FirebaseDatabase.getInstance().getReference().child("user");
        commentref = FirebaseDatabase.getInstance().getReference().child("myimages").child(postID).child("comments");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // User is signed in, proceed with commenting
                    userID = currentUser.getUid();
                    userref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String username = snapshot.child("username").getValue(String.class);
                                String imageurl = snapshot.child("user_profile_img").getValue(String.class);

                                if (username != null && imageurl != null) {
                                    // Proceed with commenting
                                    processComment(username, imageurl);
                                } else {
                                    Toast.makeText(CommentActivity.this, "Kindly set up your user profile before attempting to post a comment", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });
                } else {
                    // User is not signed in, display a message or prompt for authentication
                    Toast.makeText(CommentActivity.this, "Please sign in to comment", Toast.LENGTH_SHORT).show();
                    // You can redirect the user to the sign-in activity or provide options to sign in
                }
            }
        });

        attachRecyclerViewAdapter();
    }

    private void getPostReady(String postID, String userID){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userProfileImage = dataSnapshot.child("user_profile_img").getValue(String.class);
                    if (!userProfileImage.isEmpty()){
                        Picasso.get().load(userProfileImage).into(profileImg);
                    }
                    String name = dataSnapshot.child("username").getValue(String.class);
                    usernameview.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });

        userRef = FirebaseDatabase.getInstance().getReference().child("myimages").child(postID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try{
                        String userProfileImage = dataSnapshot.child("imageurl").getValue(String.class);
                        if (!userProfileImage.isEmpty()){
                            Picasso.get().load(userProfileImage).into(postImage);
                        }
                        else{
                            postImage.setVisibility(View.INVISIBLE);
                        }

                        String postDesc = dataSnapshot.child("postDesc").getValue(String.class);
                        if (!postDesc.isEmpty()){
                            userDesc.setText(postDesc);
                            userDesc.setVisibility(View.VISIBLE);
                        }
                        else{
                            userDesc.setVisibility(View.INVISIBLE);
                        }
                    }
                    catch (Exception e){
                        Toast.makeText(CommentActivity.this, "Some problem occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void attachRecyclerViewAdapter() {
        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(commentref, CommentModel.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<CommentModel, commentviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull commentviewholder holder, int position, @NonNull CommentModel model) {

                holder.usernameText.setText(model.getUsername());
                holder.userMessageText.setText(model.getUsermsg());
                holder.userDT.setText("Date: " + model.getDate() + " Time: " + model.getTime());
                getUserProfileImage(holder,model.getUserID());
            }

            @NonNull
            @Override
            public commentviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_comment, parent, false);
                return new commentviewholder(view);
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void processComment(String username, String imageUrl) {
        String commentpost = commentEditText.getText().toString().trim();

        if (!commentpost.isEmpty()) {
            String randomPostID = commentref.push().getKey();

            Calendar dateValue = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
            String cDate = dateFormat.format(dateValue.getTime());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String cTime = timeFormat.format(dateValue.getTime());

            HashMap<String, String> cmnt = new HashMap<>();
            cmnt.put("date", cDate);
            cmnt.put("time", cTime);
            cmnt.put("userID", userID);
            cmnt.put("username", username);
            cmnt.put("userimage", imageUrl);
            cmnt.put("usermsg", commentpost);

            commentref.child(randomPostID).setValue(cmnt).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CommentActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getUserProfileImage(commentviewholder holder, String userID){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userProfileImage = dataSnapshot.child("user_profile_img").getValue(String.class);
                    Picasso.get().load(userProfileImage).into(holder.userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }


}
