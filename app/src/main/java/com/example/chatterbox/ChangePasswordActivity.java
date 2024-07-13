package com.example.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.StartupTime;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText oldpass,newpass,newpass2;
    Button change,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        oldpass = findViewById(R.id.txtOldPass);
        newpass = findViewById(R.id.txtNewPass);
        newpass2 = findViewById(R.id.txtNewPass2);
        change = findViewById(R.id.btnChangePass);
        cancel = findViewById(R.id.btnCancel);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpassword,newpassword,newpassword2;
                oldpassword = oldpass.getText().toString();
                newpassword = newpass.getText().toString();
                newpassword2 = newpass2.getText().toString();
                if(!newpassword.equals(newpassword2)){
                    Toast.makeText(ChangePasswordActivity.this,"Passwords doesn't match!",Toast.LENGTH_SHORT).show();
                    newpass.getText().clear();
                    newpass2.getText().clear();
                    return;
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email,oldpassword);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChangePasswordActivity.this,"Password Changed!",Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(ChangePasswordActivity.this,"Password Changing Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            oldpass.getText().clear();
                            Toast.makeText(ChangePasswordActivity.this,"Authentication Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChangePasswordActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }
}