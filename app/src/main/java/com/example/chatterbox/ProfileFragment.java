package com.example.chatterbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    ImageView profilepic;
    EditText name;
    TextView username,email;
    Button update,logout,changepass,verify;
    User user;
    LinearLayout verifyLayout;
    ImageButton editpic;
    ActivityResultLauncher<Intent> pickImage;

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
        changepass = view.findViewById(R.id.cngPass);
        verify = view.findViewById(R.id.verifyBtn);
        verifyLayout = view.findViewById(R.id.verificationLayout);
        editpic = view.findViewById(R.id.editpicBtn);

        getUserData();

        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            verifyLayout.setVisibility(View.GONE);
        }

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
                Toast.makeText(getActivity(),"Logging out!",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                    verifyLayout.setVisibility(View.GONE);
                    return;
                }
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(),"Verification email sent to your email address!",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Action failed! "+ e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
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
                    Uri image = o.getData().getData();
                    profilepic.setImageURI(image);

                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), image);
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), image);
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
                            Toast.makeText(getActivity(),"Profile picture updated!",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Upload failed! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception e){
                    Toast.makeText(getActivity(),"No image selected!",Toast.LENGTH_SHORT).show();
                }
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
        FirebaseUtil.getStorageReference().child(FirebaseUtil.getProfilePicPath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilepic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Profile Picture loading failed! " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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