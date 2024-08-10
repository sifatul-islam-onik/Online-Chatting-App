package com.example.chatterbox;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

public class PostRecyclerAdapter extends FirestoreRecyclerAdapter<Post,PostRecyclerAdapter.PostViewHolder> {

    Context context;

    public PostRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostRecyclerAdapter.PostViewHolder holder, int position, @NonNull Post model) {
        holder.name.setText(model.getName());
        holder.username.setText(model.getUsername());

        FirebaseUtil.getStorageReference().child("userprofiles/"+model.getUserid()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.profilePic);
            }
        });

        if(model.getText().isEmpty()){
            holder.postTxt.setVisibility(View.GONE);
            holder.postPic.setVisibility(View.VISIBLE);
        }
        else if(model.getImgUrl().isEmpty()){
            holder.postTxt.setVisibility(View.VISIBLE);
            holder.postPic.setVisibility(View.GONE);
        }
        else{
            holder.postTxt.setVisibility(View.VISIBLE);
            holder.postPic.setVisibility(View.VISIBLE);
        }

        if(!model.getText().isEmpty()){
            holder.postTxt.setText(model.getText());
        }

        if(!model.getImgUrl().isEmpty()){
            FirebaseUtil.getStorageReference().child(model.getImgUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.postPic);
                }
            });
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_recycler,parent,false);
        return new PostRecyclerAdapter.PostViewHolder(view);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView name,username,postTxt,likeCnt;
        ImageView profilePic,postPic;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.postName);
            username = itemView.findViewById(R.id.postUsername);
            postTxt = itemView.findViewById(R.id.txtPost);
            profilePic = itemView.findViewById(R.id.profilePic);
            postPic = itemView.findViewById(R.id.imgPost);
        }
    }
}
