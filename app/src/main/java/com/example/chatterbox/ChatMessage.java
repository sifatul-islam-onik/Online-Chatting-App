package com.example.chatterbox;

import com.google.firebase.Timestamp;

public class ChatMessage {
    String message;
    String senderId;
    String photoUrl;
    Timestamp timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        photoUrl = "";
    }

    public ChatMessage(String photoUrl, Timestamp timestamp,String senderId) {
        message = "";
        this.senderId = senderId;
        this.photoUrl = photoUrl;
        this.timestamp = timestamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
