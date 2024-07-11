package com.example.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    User otherUser;
    TextView username,fullname;
    EditText msg;
    ImageButton back,send;
    RecyclerView recyclerView;

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });



    }
}