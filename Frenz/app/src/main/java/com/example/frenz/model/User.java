package com.example.frenz.model;

public class User {
    private String userID;
    private String username;
    private String userFirstname;
    private String userLastname;
    private String userEmailaddress;
    private String userPassword;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public void setUserLastname(String userLastname) {
        this.userLastname = userLastname;
    }

    public String getUserEmailaddress() {
        return userEmailaddress;
    }

    public void setUserEmailaddress(String userEmailaddress) {
        this.userEmailaddress = userEmailaddress;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public User(String userID, String username, String userFirstname, String userLastname, String userEmailaddress, String userPassword) {
        this.userID = userID;
        this.username = username;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userEmailaddress = userEmailaddress;
        this.userPassword = userPassword;
    }
}
