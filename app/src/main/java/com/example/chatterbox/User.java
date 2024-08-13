package com.example.chatterbox;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class User implements Parcelable {
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

    protected User(Parcel in) {
        email = in.readString();
        name = in.readString();
        username = in.readString();
        createdTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        userId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(username);
        parcel.writeParcelable(createdTimestamp, i);
        parcel.writeString(userId);
    }
}
