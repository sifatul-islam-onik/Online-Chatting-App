package com.example.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    EditText name,email,pass,pass2;
    Button signup;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        pass = findViewById(R.id.txtPass);
        pass2 = findViewById(R.id.txtPass2);
        signup = findViewById(R.id.btnNew);
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = name.getText().toString();
                String Email = email.getText().toString();
                String Password = pass.getText().toString();
                String Password2 = pass2.getText().toString();
                if(TextUtils.isEmpty(Name) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)){
                    Toast.makeText(SignupActivity.this,"Required fields are empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Password.equals(Password2)){
                    Toast.makeText(SignupActivity.this,"Passwords doesn't match!",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(Email,Name, Timestamp.now(),FirebaseUtil.currentUserId());
                                    FirebaseUtil.currentUser().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                                startActivity(i);
                                            }
                                            else{
                                                Toast.makeText(SignupActivity.this,"firestore error!",Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                } else {
                                    {
                                        try
                                        {
                                            throw task.getException();
                                        }
                                        // if user enters wrong email.
                                        catch (FirebaseAuthWeakPasswordException weakPassword)
                                        {
                                            Toast.makeText(SignupActivity.this, "Weak password!",Toast.LENGTH_SHORT).show();
                                        }
                                        // if user enters wrong password.
                                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                        {
                                            Toast.makeText(SignupActivity.this, "Wrong email format!",Toast.LENGTH_SHORT).show();
                                        }
                                        catch (FirebaseAuthUserCollisionException existEmail)
                                        {
                                            Toast.makeText(SignupActivity.this, "Email already iin use!",Toast.LENGTH_SHORT).show();
                                        }
                                        catch (Exception e)
                                        {
                                            Toast.makeText(SignupActivity.this, "Error!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });

            }
        });

    }
}