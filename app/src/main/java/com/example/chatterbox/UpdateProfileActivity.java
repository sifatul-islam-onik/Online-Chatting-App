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
import android.widget.TextView;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {

    ImageView profilepic;
    EditText name,username;
    Button update;
    User user;
    ImageButton editpic,cancel;
    ActivityResultLauncher<Intent> pickImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profilepic = findViewById(R.id.updateProfilePicImg);
        name = findViewById(R.id.updateProfilenameTxt);
        username = findViewById(R.id.updateProfileusernameTxt);
        update = findViewById(R.id.profileupdateBtn);
        cancel = findViewById(R.id.btnUpdateBack);
        editpic = findViewById(R.id.editpicBtn);

        getUserData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLocal();
                updateCloud();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        editpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImage.launch(i);
            }
        });

        pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                try{
                    Toast.makeText(UpdateProfileActivity.this,"Uploading Image...",Toast.LENGTH_SHORT).show();

                    Uri image = o.getData().getData();
                    profilepic.setImageURI(image);
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(UpdateProfileActivity.this.getContentResolver(), image);
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(UpdateProfileActivity.this.getContentResolver(), image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,25,baos);
                    byte[] data = baos.toByteArray();

                    FirebaseUtil.getStorageReference().child(FirebaseUtil.getProfilePicPath()).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(UpdateProfileActivity.this,"Profile picture updated!",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception e){
                    Toast.makeText(UpdateProfileActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void updateCloud() {
        FirebaseUtil.currentUser().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UpdateProfileActivity.this,"Profile updated!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(UpdateProfileActivity.this,task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void updateLocal() {
        String newName = name.getText().toString().trim();
        String newUsername = username.getText().toString().trim();
        if(newName.isEmpty() || newUsername.isEmpty()){
            Toast.makeText(UpdateProfileActivity.this,"Invalid inputs!",Toast.LENGTH_SHORT).show();
            return;
        }
        user.setName(newName);
        user.setUsername(newUsername);
    }


    void getUserData() {
        FirebaseUtil.getStorageReference().child(FirebaseUtil.getProfilePicPath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilepic);
            }
        });

        FirebaseUtil.currentUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user = task.getResult().toObject(User.class);
                    name.setText(user.getName());
                    username.setText(user.getUsername());
                }
            }
        });
    }

}