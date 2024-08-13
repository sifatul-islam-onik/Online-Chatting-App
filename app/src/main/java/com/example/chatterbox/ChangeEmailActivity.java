package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {

    EditText email,pass;
    Button change;
    ImageButton cancel;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.txtChangeEmail);
        pass = findViewById(R.id.txtChangeEmailPass);
        change = findViewById(R.id.btnChangeEmail);
        cancel = findViewById(R.id.btnChangeEmailBack);
        user = FirebaseAuth.getInstance().getCurrentUser();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = user.getEmail();
                String newEmail = email.getText().toString().trim();
                String password = pass.getText().toString();
                if(newEmail.isEmpty()){
                    email.setError("This field cannot be empty!");
                    return;
                }
                if(password.isEmpty()){
                    pass.setError("This field cannot be empty!");
                    return;
                }
                AuthCredential authCredential = EmailAuthProvider.getCredential(Email,password);
                Toast.makeText(ChangeEmailActivity.this,"Updating Email Address...",Toast.LENGTH_SHORT).show();
                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        openPromt();
                                    }
                                    else{
                                        Toast.makeText(ChangeEmailActivity.this,task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(ChangeEmailActivity.this,task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
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

    void openPromt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmailActivity.this);
        builder.setTitle("Attention!");
        builder.setIcon(R.drawable.icon_notification);
        builder.setMessage(R.string.change_email);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}