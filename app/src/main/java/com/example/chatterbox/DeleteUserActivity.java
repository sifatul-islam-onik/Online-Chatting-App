package com.example.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteUserActivity extends AppCompatActivity {

    EditText confirm,pass;
    Button delete;
    ImageButton cancel;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        confirm = findViewById(R.id.txtDeleteConfirm);
        pass = findViewById(R.id.txtDeletePass);
        delete = findViewById(R.id.btnDelete);
        cancel = findViewById(R.id.btnDeleteBack);
        user = FirebaseAuth.getInstance().getCurrentUser();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Confirm = confirm.getText().toString();
                String password = pass.getText().toString();
                String email = user.getEmail();
                if(Confirm.isEmpty() || !Confirm.equals("CONFIRM")){
                    confirm.setError("Are you not CONFIRM ?!");
                    return;
                }
                if(password.isEmpty()){
                    pass.setError("This field cannot be empty!");
                    return;
                }

                AuthCredential authCredential = EmailAuthProvider.getCredential(email,password);
                Toast.makeText(DeleteUserActivity.this,"Deleting Account...",Toast.LENGTH_SHORT).show();
                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            FirebaseUtil.allUsersCollectionReference().document(FirebaseUtil.currentUserId()).delete();

                            FirebaseUtil.allChatRoomCollectionReference().whereArrayContains("userIds",FirebaseUtil.currentUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(QueryDocumentSnapshot doc:queryDocumentSnapshots){
                                        doc.getReference().collection("chats").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for(QueryDocumentSnapshot chat:queryDocumentSnapshots){
                                                    if(!chat.getString("photoUrl").isEmpty()) FirebaseUtil.getStorageReference().child(chat.getString("photoUrl")).delete();
                                                    chat.getReference().delete();
                                                }
                                            }
                                        });
                                        doc.getReference().delete();
                                    }
                                }
                            });

                            FirebaseUtil.allPostsCollectionReference().whereEqualTo("userid",FirebaseUtil.currentUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(QueryDocumentSnapshot snap:queryDocumentSnapshots){
                                        Post post = snap.toObject(Post.class);
                                        if(!post.getImgUrl().isEmpty()){
                                            FirebaseUtil.getStorageReference().child(post.getImgUrl()).delete();
                                        }
                                        snap.getReference().delete();
                                    }
                                }
                            });

                            FirebaseUtil.getStorageReference().child(FirebaseUtil.getProfilePicPath()).delete();

                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(DeleteUserActivity.this,"Account deleted successfully! Logged out...",Toast.LENGTH_SHORT).show();
                                        FirebaseUtil.logOut();
                                        Intent i = new Intent(DeleteUserActivity.this, LoginActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(DeleteUserActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(DeleteUserActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

    }
}