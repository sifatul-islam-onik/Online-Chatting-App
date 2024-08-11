package com.example.chatterbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class OtherProfileActivity extends AppCompatActivity {

    User user;
    TextView name,email,username;
    ImageView propic;
    Button msg,post;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.otherProfilenameTxt);
        email = findViewById(R.id.otherprofileEmailTxt);
        username = findViewById(R.id.otherProfileusernameTxt);
        propic = findViewById(R.id.otherProfilePicImg);
        msg = findViewById(R.id.btnOtherProfileMsg);
        post = findViewById(R.id.btnOtherProfilePost);
        back = findViewById(R.id.btnOtherProfileBack);

        Intent i = getIntent();

        FirebaseUtil.getUser(i.getStringExtra("userid")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setText(documentSnapshot.getString("username"));
                name.setText(documentSnapshot.getString("name"));
                email.setText(documentSnapshot.getString("email"));
            }
        });

        FirebaseUtil.getStorageReference().child("userprofiles/"+i.getStringExtra("userid")+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(propic);
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(OtherProfileActivity.this, ChatActivity.class);
                i2.putExtra("name",i.getStringExtra("name"));
                i2.putExtra("email",i.getStringExtra("email"));
                i2.putExtra("username",i.getStringExtra("username"));
                i2.putExtra("userid",i.getStringExtra("userid"));
                startActivity(i2);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(OtherProfileActivity.this, PostActivity.class);
                i2.putExtra("userid",i.getStringExtra("userid"));
                startActivity(i2);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

    }

}