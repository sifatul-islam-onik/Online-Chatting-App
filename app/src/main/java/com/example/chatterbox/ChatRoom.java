package com.example.chatterbox;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoom {
    String chatRoomId;
    List<String>userIds;
    Timestamp lastMsgTime;
    String lastMsg;
    String lastMsgSenderId;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomId, List<String> userIds, Timestamp lastMsgTime, String lastMsgSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMsgTime = lastMsgTime;
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(Timestamp lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getLastMsgSenderId() {
        return lastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        this.lastMsgSenderId = lastMsgSenderId;
    }

}
