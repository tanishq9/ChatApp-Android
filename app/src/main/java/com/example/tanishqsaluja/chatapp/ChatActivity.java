package com.example.tanishqsaluja.chatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanishqsaluja on 17/2/18.
 */

public class ChatActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    DatabaseReference databaseReference;
    String friendUserID, currentUserID;
    ImageButton add, send;
    EditText entermessage;
    RecyclerView recyclerView;
    ArrayList<Message> arrayList;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.mybar);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = firebaseUser.getUid();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rvmessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(arrayList);
        recyclerView.setAdapter(messageAdapter);

        add = findViewById(R.id.add);
        send = findViewById(R.id.send);
        entermessage = findViewById(R.id.entermessage);


        friendUserID = getIntent().getStringExtra("friend");
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                /*databaseReference.child(friendUserID).child("last_seen").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.child(currentUserID).child("last_seen").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("test","chat instantiated.");
                            }
                        });
                    }
                });*/
                if (!dataSnapshot.hasChild(friendUserID)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("last_seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map userMap = new HashMap();//"Chat/" + friendUserID +
                    userMap.put(currentUserID + "/" + friendUserID, chatAddMap);
                    userMap.put(friendUserID + "/" + currentUserID, chatAddMap);
                    databaseReference.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.e("test", databaseError.getMessage());
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //function to retreive the messages

        loadmessages();

    }

    private void loadmessages() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(currentUserID).child(friendUserID);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                arrayList.add(message);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {
        String message = entermessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "/messages/" + currentUserID + "/" + friendUserID + "/";
            String friend_user = "/messages/" + friendUserID + "/" + currentUserID + "/";
            DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference()
                    .child(current_user_ref).child(friend_user).push();
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("type", "text");
            messageMap.put("from", friendUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + push_id, messageMap);
            messageUserMap.put(friend_user + push_id, messageMap);

            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("test", databaseError.getMessage());
                    }
                }
            });
        }

        entermessage.setText("");
    }
}

