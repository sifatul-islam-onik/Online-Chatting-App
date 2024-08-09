package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    ImageView profilepic;
    TextView name,username,email;
    Button verify,posts;
    User user;
    LinearLayout verifyLayout;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilepic = view.findViewById(R.id.ProfilePicImg);
        name = view.findViewById(R.id.ProfilenameTxt);
        username = view.findViewById(R.id.ProfileusernameTxt);
        email = view.findViewById(R.id.profileEmailTxt);
        verify = view.findViewById(R.id.verifyBtn);
        posts = view.findViewById(R.id.btnProfilePost);
        verifyLayout = view.findViewById(R.id.verificationLayout);

        getUserData();

        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            verifyLayout.setVisibility(View.GONE);
        }

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        openPromt();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Action failed! "+ e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(getActivity(), PostActivity.class);
                i2.putExtra("userid",user.getUserId());
                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i2);
            }
        });

        return view;
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
                user = task.getResult().toObject(User.class);
                user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                name.setText(user.getName());
                username.setText(user.getUsername());
                email.setText(user.getEmail());
            }
        });
    }

    void openPromt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attention!");
        builder.setIcon(R.drawable.icon_notification);
        builder.setMessage(R.string.verified_email);
        builder.setCancelable(false);
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(),"Logging out...",Toast.LENGTH_SHORT).show();
                FirebaseUtil.logOut();
                Intent ii = new Intent(getActivity(), LoginActivity.class);
                ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(ii);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}