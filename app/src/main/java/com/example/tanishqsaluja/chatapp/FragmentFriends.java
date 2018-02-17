package com.example.tanishqsaluja.chatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tanishqsaluja on 11/2/18.
 */

public class FragmentFriends extends Fragment {
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    View view;
    Query query;
    String currentuser_id;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);

        recyclerView = view.findViewById(R.id.rv);

        firebaseAuth = FirebaseAuth.getInstance();
        currentuser_id = firebaseAuth.getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        //the query which we are passing
        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friend_List")
                .child(currentuser_id);

        FirebaseRecyclerOptions<Friend> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend,FriendViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull Friend model) {
                holder.name.setText(model.getDate());
            }

            @Override
            public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.e("TEST", "OnCreate was called:");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_layout, parent, false);
                return new FriendViewHolder(view);
            }
        };

        Log.e("TEST", "" + query);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        CircleImageView circleImageView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
            status = itemView.findViewById(R.id.userstatus);
            circleImageView = itemView.findViewById(R.id.imageview);
        }
    }
}
