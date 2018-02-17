package com.example.tanishqsaluja.chatapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tanishqsaluja on 11/2/18.
 */

public class FragmentFriends extends Fragment {
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    View view;
    Query query;
    String currentuser_id,friend_user;
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

        //query
        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friend_List")
                .child(currentuser_id);

        FirebaseRecyclerOptions<Friend> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder holder, int position, @NonNull final Friend model) {
                holder.name.setText(model.getDate());
                friend_user = getRef(position).getKey();
                DatabaseReference friendref = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(friend_user);
                friendref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("name").getValue().toString();
                        String userstatus = dataSnapshot.child("status").getValue().toString();
                        holder.name.setText(username);
                        holder.status.setText(userstatus);
                        if (dataSnapshot.child("online").getValue(Boolean.class).equals(true)) {
                            holder.onlineoroffine.setImageResource(R.drawable.on);
                        } else if (dataSnapshot.child("online").getValue(Boolean.class).equals(false)) {
                            holder.onlineoroffine.setImageResource(R.drawable.off);
                        }
                        try {
                            Picasso.with(getContext()).load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.bot).into(holder.circleImageView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.e("TEST", "OnCreate was called:");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_layout, parent, false);
   /*             view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                        chatIntent.putExtra("friend",friend_user);
                        chatIntent.putExtra("name",);
                        startActivity(chatIntent);
                    }
                });*/

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
        ImageView onlineoroffine;
        CircleImageView circleImageView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
            status = itemView.findViewById(R.id.userstatus);
            circleImageView = itemView.findViewById(R.id.imageview);
            onlineoroffine = itemView.findViewById(R.id.onlineoroffline);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                    chatIntent.putExtra("friend",friend_user);
                    chatIntent.putExtra("name",name.getText().toString());
                    startActivity(chatIntent);
                }
            });
        }
    }
}
