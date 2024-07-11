package com.example.chatterbox;

import com.google.firebase.Timestamp;

public class User {
    private String email;
    private String name;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;

    public User() {
    }

    public User(String email, String name, String username, Timestamp createdTimestamp, String userId) {
        this.email = email;
        this.name = name;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
