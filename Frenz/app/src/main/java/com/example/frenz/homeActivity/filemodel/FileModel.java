package com.example.frenz.homeActivity.filemodel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class FileModel {
    String Imageurl, postDesc, postID;
    String userID;

    public FileModel() {
    }

    public String getPostID() {
        return postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setPostID(String postID) {
        this.postID = postID;
    }
    public FileModel(String postDesc, String imageurl) {
        this.postDesc = postDesc;
        Imageurl = imageurl;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }
}
