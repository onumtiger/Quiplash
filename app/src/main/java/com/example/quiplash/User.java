package com.example.quiplash;


public class User {

    private String userID;
    private String userName;


    public User(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public User(String userID) {
        this.userID = userID;
    }

    public User() {
    }


    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }


    public void setUserID(String uid) {
        userID = uid;
    }

    public void setUserName(String name) {
        userName = name;
    }
}
