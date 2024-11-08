package com.example.frenz.homeActivity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenz.R;
import com.example.frenz.homeActivity.filemodel.FileModel;
import com.example.frenz.homeActivity.filemodel.MyViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("likes");

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.fragment_home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<FileModel> options =
                new FirebaseRecyclerOptions.Builder<FileModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("myimages"), FileModel.class)
                        .build();

        FirebaseRecyclerAdapter<FileModel, MyViewHolder> fileModelMyViewHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FileModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull FileModel model) {
                holder.preparePost(model.getPostDesc(), model.getImageurl(), model.getUserID());

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userID = firebaseUser.getUid();
                String postID = getRef(position).getKey();
                holder.getLikeButtonStatus(postID, userID);

//                String timeAgo = (String) DateUtils.getRelativeTimeSpanString(model.getTimeAdded().getSeconds() * 1000);
//                holder.dateAdded.setText(timeAgo);

                holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), CommentActivity.class)
                                .putExtra("postID",postID));
                    }
                });

                holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeReference.child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(userID)) {
                                    likeReference.child(postID).child(userID).removeValue()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    // Dislike completed
                                                    Log.d("appcheck", "Post disliked");
                                                } else {
                                                    // Handle error
                                                    Log.e("appcheck", "Error disliking post: " + task.getException());
                                                }
                                            });
                                } else {
                                    likeReference.child(postID).child(userID).setValue(true)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    // Like completed
                                                    Log.d("appcheck", "Post liked");
                                                } else {
                                                    // Handle error
                                                    Log.e("appcheck", "Error liking post: " + task.getException());
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                                Log.e("appcheck", "Database error: " + error.getMessage());
                            }
                        });
                    }
                });
            }


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_home_fragment,parent,false);
                return new MyViewHolder(view1);
            }
        };

        fileModelMyViewHolderFirebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(fileModelMyViewHolderFirebaseRecyclerAdapter);
        return view;
    }
}