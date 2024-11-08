package com.example.frenz.homeActivity.filemodel;

public class CommentModel {
    String date, time, userID, userimg, usermsg, username;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getUsermsg() {
        return usermsg;
    }

    public void setUsermsg(String usermsg) {
        this.usermsg = usermsg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CommentModel(String date, String time, String userID, String userimg, String usermsg, String username) {
        this.date = date;
        this.time = time;
        this.userID = userID;
        this.userimg = userimg;
        this.usermsg = usermsg;
        this.username = username;
    }

    public CommentModel() {
    }
}
