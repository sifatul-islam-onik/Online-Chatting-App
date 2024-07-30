package com.example.chatterbox;

import android.content.Context;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoom, RecentChatRecyclerAdapter.ChatRoomViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoom> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position, @NonNull ChatRoom model) {
        FirebaseUtil.getOtherUser(model.getUserIds()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User otheruser = task.getResult().toObject(User.class);
                    if(model.getLastMsgSenderId().equals(FirebaseUtil.currentUserId())){
                        if(!model.getLastUrl().isEmpty()){
                            holder.lastmsgTxt.setText("You: (photo)");
                        }
                        else {
                            holder.lastmsgTxt.setText("You: " + model.getLastMsg());
                        }
                    }
                    else{
                        if(!model.getLastUrl().isEmpty()){
                            holder.lastmsgTxt.setText("(photo)");
                        }
                        else{
                            holder.lastmsgTxt.setText(model.getLastMsg());
                        }
                    }
                    holder.fullnameTxt.setText(otheruser.getName());
                    holder.lastmsgTime.setText(FirebaseUtil.timestampToString(model.getLastMsgTime()));
                    FirebaseUtil.getStorageReference().child("userprofiles/"+otheruser.getUserId()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.profilePic);
                        }
                    });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, ChatActivity.class);
                            i.putExtra("name",otheruser.getName());
                            i.putExtra("email",otheruser.getEmail());
                            i.putExtra("username",otheruser.getUsername());
                            i.putExtra("userid",otheruser.getUserId());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    });
                }
            }
        });
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler,parent,false);
        return new ChatRoomViewHolder(view);
    }

    class ChatRoomViewHolder extends RecyclerView.ViewHolder{

        TextView lastmsgTxt,fullnameTxt,lastmsgTime;
        ImageView profilePic;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            lastmsgTxt = itemView.findViewById(R.id.lastMsgTxt);
            fullnameTxt = itemView.findViewById(R.id.fullNameTxt);
            lastmsgTime = itemView.findViewById(R.id.lastMsgtimeTxt);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }
}
