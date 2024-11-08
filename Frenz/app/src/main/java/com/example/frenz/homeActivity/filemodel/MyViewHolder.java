package com.example.frenz.homeActivity.filemodel;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenz.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView postDescTextView, likeTextView, usernameTextview;
    private ShapeableImageView userimageview;
    private ImageView imageView;
    public ImageView likeBtn, commentBtn;
    private ProgressBar progressBar;
    private DatabaseReference likeReference;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        postDescTextView = itemView.findViewById(R.id.post_descption_sigle_row);
        imageView = itemView.findViewById(R.id.image_to_be_shown);
        progressBar = itemView.findViewById(R.id.imageLoader);
        likeBtn = itemView.findViewById(R.id.likeBtn);
        likeTextView = itemView.findViewById(R.id.like_text);
        commentBtn = itemView.findViewById(R.id.commentBtn);
        userimageview = itemView.findViewById(R.id.single_row_profile_pic);
        usernameTextview = itemView.findViewById(R.id.single_row_username);
    }

    public void preparePost(String postDesc, String imageURl, String userID) {

        getImageAndUserSet(userID);

        if (postDesc != null) {
            postDescTextView.setText(postDesc);
            postDescTextView.setVisibility(View.VISIBLE);
        } else if (postDesc == null) {
            postDescTextView.setVisibility(View.GONE);
        }

        if (imageURl != null) {
            Picasso.get().load(imageURl).fit().placeholder(R.drawable.add_image_placeholder)
                    .error(R.drawable.add_image_placeholder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            Log.d("appcheck", "Image visible");  // Updated Log statement with "appcheck" tag
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.d("appcheck", "Error loading image: " + e.getMessage());  // Updated Log statement with "appcheck" tag
                        }
                    });
        } else if (imageURl == null) {
            // If imageUrl is null, hide the imageView
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }
    }

    public void getLikeButtonStatus(String postID, String userID) {
        likeReference = FirebaseDatabase.getInstance().getReference("likes");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postID).hasChild(userID)) {
                    int likeCount = (int) snapshot.child(postID).getChildrenCount();
                    likeTextView.setText(String.valueOf(likeCount));  // Ensure likeCount is converted to String
                    likeBtn.setImageResource(R.drawable.heart_like_filled);
                    Log.d("appcheck", "User liked the post");  // Updated Log statement with "appcheck" tag
                } else {
                    int likeCount = (int) snapshot.child(postID).getChildrenCount();
                    likeTextView.setText(String.valueOf(likeCount));  // Ensure likeCount is converted to String
                    likeBtn.setImageResource(R.drawable.like_unfilled);
                    Log.d("appcheck", "User did not like the post");  // Updated Log statement with "appcheck" tag
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("appcheck", "DatabaseError: " + error.getMessage());  // Updated Log statement with "appcheck" tag
            }
        });
    }

    private void getImageAndUserSet(String userID){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userProfileImage = dataSnapshot.child("user_profile_img").getValue(String.class);
                    Picasso.get().load(userProfileImage).into(userimageview);
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
