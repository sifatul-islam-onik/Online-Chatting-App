package com.example.chatterbox;

import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {
    public static  String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUser(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static void logOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static CollectionReference allUsersCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference allPostsCollectionReference(){
        return FirebaseFirestore.getInstance().collection("posts");
    }

    public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatRoomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }

    public static String getChatRoomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()) return userId1+"_"+userId2;
        else return userId2+"_"+userId1;
    }

    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUser(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())) return allUsersCollectionReference().document(userIds.get(1));
        else return allUsersCollectionReference().document(userIds.get(0));
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("dd-MM-yy (HH:mm)").format(timestamp.toDate());
    }

    public static String getProfilePicPath(){
        return "userprofiles/" + FirebaseUtil.currentUserId() + ".jpg";
    }

    public static StorageReference getStorageReference(){
        return FirebaseStorage.getInstance().getReference();
    }

}
