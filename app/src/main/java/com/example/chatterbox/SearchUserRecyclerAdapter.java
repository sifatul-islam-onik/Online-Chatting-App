package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User, SearchUserRecyclerAdapter.UserViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.fullnameTxt.setText(model.getName());
        FirebaseUtil.getStorageReference().child("userprofiles/"+model.getUserId()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.profilePic);
            }
        });
        if(model.getUserId().equals(FirebaseUtil.currentUserId())){
            holder.usernameTxt.setText(model.getUsername()+" (Me)");
        }
        else{
            holder.usernameTxt.setText(model.getUsername());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("name",model.getName());
                i.putExtra("email",model.getEmail());
                i.putExtra("username",model.getUsername());
                i.putExtra("userid",model.getUserId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler,parent,false);
        return new UserViewHolder(view);
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView usernameTxt,fullnameTxt;
        ImageView profilePic;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTxt = itemView.findViewById(R.id.userName);
            fullnameTxt = itemView.findViewById(R.id.fullName);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }
}
