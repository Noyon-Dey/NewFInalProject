package com.example.bitmproject.tourmate.PosoClass;

import java.io.Serializable;

public class UserClass implements Serializable {

    private String userId;
    private String userName;
    private String userEmail;

    public UserClass(String userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public UserClass(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public UserClass() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
