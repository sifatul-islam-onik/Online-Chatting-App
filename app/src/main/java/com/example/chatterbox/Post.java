package com.example.chatterbox;

import com.google.firebase.Timestamp;

public class Post {
    String text,imgUrl,name,username,userid,timestamp;

    public Post() {
    }

    public Post(String text, String imgUrl, String name, String username, String userid, String timestamp) {
        this.text = text;
        this.imgUrl = imgUrl;
        this.name = name;
        this.username = username;
        this.userid = userid;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
