package com.example.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    User otherUser;
    String chatRoomId;
    ChatRoom chatRoom;
    TextView username,fullname;
    EditText msg;
    ImageButton back,send;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;

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
        recyclerView = findViewById(R.id.recyclerView);
        otherUser = new User();

        Intent i = getIntent();

        otherUser.setUserId(i.getStringExtra("userid"));
        otherUser.setUsername(i.getStringExtra("username"));
        otherUser.setName(i.getStringExtra("name"));
        otherUser.setEmail(i.getStringExtra("email"));
        username.setText(otherUser.getUsername());
        fullname.setText(otherUser.getName());

        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        getChatRoom();
        setupView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msg.getText().toString().trim();
                if(message.isEmpty()) return;
                sendMsg(message);
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

    void sendMsg(String message) {
        chatRoom.setLastMsgSenderId(FirebaseUtil.currentUserId());
        chatRoom.setLastMsg(message);
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
}