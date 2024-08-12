package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        holder.time.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        holder.like.setText(Integer.toString(model.getLike())+" likes");

        if(model.getLikeids().contains(FirebaseUtil.currentUserId())){
            holder.likebtn.setBackgroundColor(context.getResources().getColor(R.color.splashback));
        }
        else{
            holder.likebtn.setBackgroundColor(context.getResources().getColor(R.color.background));
        }

        FirebaseUtil.getStorageReference().child("userprofiles/"+model.getUserid()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.profilePic);
            }
        });

        if(model.getUserid().equals(FirebaseUtil.currentUserId())) holder.delete.setVisibility(View.VISIBLE);
        else holder.delete.setVisibility(View.GONE);

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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtil.allPostsCollectionReference().document(model.getPostid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.getString("imgUrl").isEmpty()) FirebaseUtil.getStorageReference().child(documentSnapshot.getString("imgUrl")).delete();
                        documentSnapshot.getReference().delete();
                        Toast.makeText(context, "Post deleted...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.getLikeids().contains(FirebaseUtil.currentUserId())){
                    model.setLike(model.getLike()-1);
                    List<String>l = model.getLikeids();
                    l.remove(FirebaseUtil.currentUserId());
                    model.setLikeids(l);
                    FirebaseUtil.allPostsCollectionReference().document(model.getPostid()).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            holder.likebtn.setBackgroundColor(context.getResources().getColor(R.color.background));
                        }
                    });
                }
                else{
                    model.setLike(model.getLike()+1);
                    List<String>l = model.getLikeids();
                    l.add(FirebaseUtil.currentUserId());
                    model.setLikeids(l);
                    FirebaseUtil.allPostsCollectionReference().document(model.getPostid()).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            holder.likebtn.setBackgroundColor(context.getResources().getColor(R.color.splashback));
                        }
                    });
                }
                holder.like.setText(Integer.toString(model.getLike())+" likes");
            }
        });

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_recycler,parent,false);
        return new PostRecyclerAdapter.PostViewHolder(view);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView name,username,postTxt,time,like;
        ImageView profilePic,postPic,delete;
        ImageButton likebtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.postName);
            username = itemView.findViewById(R.id.postUsername);
            postTxt = itemView.findViewById(R.id.txtPost);
            profilePic = itemView.findViewById(R.id.profilePic);
            postPic = itemView.findViewById(R.id.imgPost);
            delete = itemView.findViewById(R.id.btnPostDelete);
            time = itemView.findViewById(R.id.txtPostTime);
            like = itemView.findViewById(R.id.txtPostLike);
            likebtn = itemView.findViewById(R.id.btnPostLike);
        }
    }

}
