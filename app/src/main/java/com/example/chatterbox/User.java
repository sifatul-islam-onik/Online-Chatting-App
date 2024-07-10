package com.example.chatterbox;

import com.google.firebase.Timestamp;

public class User {
    private String email;
    private String name;
    private Timestamp createdTimestamp;
    private String userId;

    public User(String email, String name, Timestamp createdTimestamp, String userId) {
        this.email = email;
        this.name = name;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
