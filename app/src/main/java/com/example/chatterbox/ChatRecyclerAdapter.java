package com.example.chatterbox;

import static android.view.View.GONE;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatRecyclerAdapter.ChatViewHolder> {

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatMessage model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChat.setVisibility(GONE);
            holder.lefttime.setVisibility(GONE);
            holder.rightChat.setVisibility(View.VISIBLE);
            holder.righttime.setVisibility(View.VISIBLE);
            holder.rightmsg.setText(model.getMessage());
            holder.righttime.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
            if(model.getPhotoUrl().isEmpty()){
                holder.rightphoto.setVisibility(GONE);
                holder.rightmsg.setVisibility(View.VISIBLE);
            }
            else{
                holder.rightphoto.setVisibility(View.VISIBLE);
                holder.rightmsg.setVisibility(GONE);
                FirebaseUtil.getStorageReference().child(model.photoUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.rightphoto);
                    }
                });
            }
        }
        else{
            holder.leftChat.setVisibility(View.VISIBLE);
            holder.lefttime.setVisibility(View.VISIBLE);
            holder.rightChat.setVisibility(GONE);
            holder.righttime.setVisibility(GONE);
            holder.leftmsg.setText(model.getMessage());
            holder.lefttime.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
            if(model.getPhotoUrl().isEmpty()){
                holder.leftphoto.setVisibility(GONE);
                holder.leftmsg.setVisibility(View.VISIBLE);
            }
            else{
                holder.leftphoto.setVisibility(View.VISIBLE);
                holder.leftmsg.setVisibility(GONE);
                FirebaseUtil.getStorageReference().child(model.photoUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.leftphoto);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler,parent,false);
        return new ChatViewHolder(view);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChat,rightChat;
        TextView leftmsg,rightmsg,lefttime,righttime;
        ImageView leftphoto,rightphoto;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChat = itemView.findViewById(R.id.otherLayout);
            rightChat = itemView.findViewById(R.id.myLayout);
            leftmsg = itemView.findViewById(R.id.otherTxt);
            rightmsg = itemView.findViewById(R.id.myTxt);
            leftphoto = itemView.findViewById(R.id.otherPhoto);
            rightphoto = itemView.findViewById(R.id.myPhoto);
            lefttime = itemView.findViewById(R.id.txtOtherTime);
            righttime = itemView.findViewById(R.id.txtMyTime);
        }
    }
}
