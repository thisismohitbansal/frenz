package com.example.frenz.homeActivity.filemodel;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenz.R;
import com.google.android.material.imageview.ShapeableImageView;

public class commentviewholder extends RecyclerView.ViewHolder {
    public ShapeableImageView userImage;
    public TextView usernameText;
    public TextView userMessageText;
    public TextView userDT;
    public commentviewholder(@NonNull View itemView) {
        super(itemView);
        userImage = itemView.findViewById(R.id.comment_single_row_user_image);
        usernameText = itemView.findViewById(R.id.comment_single_row_user_name);
        userMessageText = itemView.findViewById(R.id.userMessage);
        userDT = itemView.findViewById(R.id.userTime);

    }
}
