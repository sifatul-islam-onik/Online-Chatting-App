package com.example.chatterbox;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    ImageView profilepic;
    EditText name;
    TextView username,email;
    Button update,logout;
    User user;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilepic = view.findViewById(R.id.profilepicImg);
        name = view.findViewById(R.id.profilenameTxt);
        username = view.findViewById(R.id.profileusernameTxt);
        email = view.findViewById(R.id.profileemailTxt);
        update = view.findViewById(R.id.profileupdateBtn);
        logout = view.findViewById(R.id.profilelogoutBtn);

        getUserData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLocal();
                updateCloud();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtil.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        return view;
    }

    void updateCloud() {
        FirebaseUtil.currentUser().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Profile updated!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"Failed to update profile!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void updateLocal() {
        String newName = name.getText().toString().trim();
        String newUsername = username.getText().toString().trim();
        if(newName.isEmpty() || newUsername.isEmpty()){
            Toast.makeText(getActivity(),"Invalid inputs!",Toast.LENGTH_SHORT).show();
            return;
        }
        user.setName(newName);
        user.setUsername(newUsername);
    }


    void getUserData() {
        FirebaseUtil.currentUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user = task.getResult().toObject(User.class);
                name.setText(user.getName());
                username.setText(user.getUsername());
                email.setText(user.getEmail());
            }
        });
    }
}