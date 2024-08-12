package com.example.chatterbox;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Post {
    String name,username,text,imgUrl,postid,userid;
    Timestamp timestamp;
    List<String>likeids;
    int like;

    public Post() {
    }

    public Post(String name,String username, String userid,Timestamp timestamp) {
        this.text = "";
        this.imgUrl = "";
        like = 0;
        this.userid = userid;
        this.timestamp = timestamp;
        this.name = name;
        this.username = username;
        this.likeids = new ArrayList<String>();
    }

    public List<String> getLikeids() {
        return likeids;
    }

    public void setLikeids(List<String> likeids) {
        this.likeids = likeids;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
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

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
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
