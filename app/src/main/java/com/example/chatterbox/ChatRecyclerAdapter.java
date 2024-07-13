package com.example.chatterbox;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

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
            holder.rightChat.setVisibility(View.VISIBLE);
            holder.rightmsg.setText(model.getMessage());
        }
        else{
            holder.leftChat.setVisibility(View.VISIBLE);
            holder.rightChat.setVisibility(GONE);
            holder.leftmsg.setText(model.getMessage());
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
        TextView leftmsg,rightmsg;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChat = itemView.findViewById(R.id.otherLayout);
            rightChat = itemView.findViewById(R.id.myLayout);
            leftmsg = itemView.findViewById(R.id.otherTxt);
            rightmsg = itemView.findViewById(R.id.myTxt);


        }
    }
}
