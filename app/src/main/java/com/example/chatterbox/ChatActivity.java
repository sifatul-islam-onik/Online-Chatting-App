package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    User otherUser;
    String chatRoomId;
    ChatRoom chatRoom;
    TextView username,fullname;
    EditText msg;
    ImageButton back,send,photo;
    ImageView profilePic;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;
    ActivityResultLauncher<Intent> pickImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.txtusernameChat);
        fullname = findViewById(R.id.txtNameChat);
        msg = findViewById(R.id.txtMsg);
        back = findViewById(R.id.btnBackChat);
        send = findViewById(R.id.btnSend);
        photo = findViewById(R.id.btnAddphoto);
        profilePic = findViewById(R.id.profilePic);
        recyclerView = findViewById(R.id.recyclerView);
        otherUser = new User();

        Intent i = getIntent();

        otherUser.setUserId(i.getStringExtra("userid"));
        otherUser.setUsername(i.getStringExtra("username"));
        otherUser.setName(i.getStringExtra("name"));
        otherUser.setEmail(i.getStringExtra("email"));
        username.setText(otherUser.getUsername());
        fullname.setText(otherUser.getName());

        FirebaseUtil.getStorageReference().child("users/"+otherUser.getUserId()+"/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }
        });

        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        getChatRoom();
        setupView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                    openPromt();
                    return;
                }
                String message = msg.getText().toString().trim();
                if(message.isEmpty()) return;
                sendMsg(message);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                    openPromt();
                    return;
                }
                sendPhoto();
            }
        });

        pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                try{
                    Uri image = o.getData().getData();
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getApplicationContext().getContentResolver(), image);
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,25,baos);
                    byte[] data = baos.toByteArray();
                    Toast.makeText(ChatActivity.this,"Uploading photo...",Toast.LENGTH_SHORT).show();
                    FirebaseUtil.getStorageReference().child("photoMsgs/" + chatRoom.getChatRoomId() + chatRoom.lastMsgTime.toString() + ".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            chatRoom.setLastUrl("photoMsgs/" + chatRoom.getChatRoomId() + chatRoom.lastMsgTime.toString() + ".jpg");
                            FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);
                            ChatMessage chatMessage = new ChatMessage(chatRoom.getLastUrl(),chatRoom.lastMsgTime,chatRoom.lastMsgSenderId);
                            FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChatActivity.this,"Photo sent! " ,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this,"Upload failed! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception e){
                    Toast.makeText(ChatActivity.this,"No image selected!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupView() {
        Query query = FirebaseUtil.getChatRoomMessageReference(chatRoomId).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>().setQuery(query, ChatMessage.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendPhoto(){
        if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            openPromt();
            return;
        }
        chatRoom.setLastMsgSenderId(FirebaseUtil.currentUserId());
        chatRoom.setLastMsgTime(Timestamp.now());
        chatRoom.setLastMsg("");
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImage.launch(i);
    }

    void sendMsg(String message) {
        if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            openPromt();
            return;
        }
        chatRoom.setLastMsgSenderId(FirebaseUtil.currentUserId());
        chatRoom.setLastMsg(message);
        chatRoom.setLastUrl("");
        chatRoom.setLastMsgTime(Timestamp.now());
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);
        ChatMessage chatMessage = new ChatMessage(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    msg.setText("");
                }
            }
        });

    }

    void getChatRoom(){
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    chatRoom = task.getResult().toObject(ChatRoom.class);
                    if(chatRoom==null){
                        chatRoom = new ChatRoom(chatRoomId, Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()), Timestamp.now(),"");
                        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);
                    }
                }
            }
        });
    }

    void openPromt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Attention!");
        builder.setIcon(R.drawable.icon_notification);
        builder.setMessage("You need to verify your email address before you can message someone! Verify your account from profile section...");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}