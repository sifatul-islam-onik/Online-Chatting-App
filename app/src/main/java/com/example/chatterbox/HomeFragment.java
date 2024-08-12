package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    Button post;
    RecyclerView recyclerView;
    PostRecyclerAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerHome);
        post = view.findViewById(R.id.btnPost);
        setupView();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                    openPromt();
                    return;
                }
                Intent i = new Intent(getActivity(), CreatePostActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        return view;
    }

    void setupView(){
        Query query = FirebaseUtil.allPostsCollectionReference().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post.class).build();
        adapter = new PostRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null) adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null) adapter.notifyDataSetChanged();
    }

    void openPromt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Attention!");
        builder.setIcon(R.drawable.icon_notification);
        builder.setMessage(R.string.verify_email);
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}