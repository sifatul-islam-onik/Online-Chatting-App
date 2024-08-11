package com.example.chatterbox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreatePostActivity extends AppCompatActivity {

    User user;
    Post post;
    String time;
    ImageButton back;
    ImageView photo;
    EditText posttxt;
    Button addPic,btnpost;
    ActivityResultLauncher<Intent> pickImage;
    Boolean flag;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        time = "";flag = false;
        back = findViewById(R.id.btnCreatePostBack);
        posttxt = findViewById(R.id.txtcreatePost);
        addPic = findViewById(R.id.btnCreatePhoto);
        btnpost = findViewById(R.id.btnCreatePost);
        photo = findViewById(R.id.createphoto);
        photo.setVisibility(View.GONE);

        FirebaseUtil.currentUser().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImage.launch(i);
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = posttxt.getText().toString();
                if(text.isEmpty() && !flag){
                    return;
                }
                Timestamp timex = Timestamp.now();
                time = timex.toString();
                post = new Post(user.getName(), user.getUsername(), user.getUserId(), timex);

                if(!text.isEmpty()) post.setText(text);
                if(flag){
                    post.setImgUrl("postPics/"+user.getUserId()+time+".jpg");
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(CreatePostActivity.this.getContentResolver(), image);
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(CreatePostActivity.this.getContentResolver(), image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,25,baos);
                    byte[] data = baos.toByteArray();
                    FirebaseUtil.getStorageReference().child(post.getImgUrl()).putBytes(data).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatePostActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                FirebaseUtil.allPostsCollectionReference().add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreatePostActivity.this,"Post Uploaded...",Toast.LENGTH_SHORT).show();
                        post.setPostid(documentReference.getId());
                        documentReference.set(post);
                        startActivity(new Intent(CreatePostActivity.this,MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreatePostActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                try{
                    flag = true;
                    photo.setVisibility(View.VISIBLE);
                    image = o.getData().getData();
                    photo.setImageURI(image);

                }catch(Exception e){
                    Toast.makeText(CreatePostActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}