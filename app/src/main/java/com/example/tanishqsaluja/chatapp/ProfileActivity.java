package com.example.tanishqsaluja.chatapp;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tanishqsaluja on 17/2/18.
 */

public class ProfileActivity extends AppCompatActivity {
    TextView name, status, count;
    Button send, decline;
    CircleImageView circleImageView;
    String current_state = "not_friends";
    //total 3 states-not friends , req_sent ,req_received
    DatabaseReference friendDatabase, friendList;
    //friend database is the friend request database and friendlist is the database for all friends
    String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = findViewById(R.id.name);
        status = findViewById(R.id.status);
        count = findViewById(R.id.count);
        circleImageView = findViewById(R.id.image);
        send = findViewById(R.id.sendrequest);
        decline = findViewById(R.id.declinerequest);
        decline.setVisibility(View.INVISIBLE);
        decline.setEnabled(false);
        count.setVisibility(View.INVISIBLE);
        uid = getIntent().getStringExtra("uid");
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("test", uid);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        //to retreive the data , add listener to that reference
        if(currentUser.getUid().equals(uid)){
            send.setVisibility(View.INVISIBLE);
            send.setEnabled(false);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue(String.class);
                String userstatus = dataSnapshot.child("status").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.bot).into(circleImageView);
                name.setText(username);
                status.setText(userstatus);

                friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
                friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(uid)) {
                            String request_type = dataSnapshot.child(uid).child("request_type").getValue(String.class);
                            if (request_type.equals("received")) {
                                current_state = "req_received";
                                send.setText("Accept Friend Request");
                            } else if (request_type.equals("sent")) {
                                current_state = "req_sent";
                                send.setText("Cancel Friend Request");
                            }
                        } else {
                            friendList = FirebaseDatabase.getInstance().getReference().child("Friend_List");
                            friendList.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(uid)) {
                                        current_state = "friends";
                                        send.setText("Unfriend");// + getIntent().getStringExtra("uidname"));
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //not friends state
                send.setEnabled(false);
                if (current_state.equals("not_friends")) {
                    friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
                    friendDatabase.child(currentUser.getUid()).child(uid)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendDatabase.child(uid).child(currentUser.getUid())
                                        .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(ProfileActivity.this, "Request sent successfully!", Toast.LENGTH_SHORT).show();

                                        send.setEnabled(true);
                                        current_state = "req_sent";
                                        send.setText("Cancel Friend Request");
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //reqeust_sent state
                else if (current_state.equals("req_sent")) {
                    friendDatabase.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    send.setEnabled(true);
                                    current_state = "not_friends";
                                    send.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }
                //request_received state
                else if (current_state.equals("req_received")) {
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    friendList = FirebaseDatabase.getInstance().getReference().child("Friend_List");
                    friendList.child(currentUser.getUid()).child(uid).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendList.child(uid).child(currentUser.getUid()).child("date")
                                    .setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendDatabase.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendDatabase.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    send.setEnabled(true);
                                                    current_state = "friends";
                                                    send.setText("Unfriend " + getIntent().getStringExtra("uidname"));
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
                else if(current_state.equals("friends")){
                    friendList = FirebaseDatabase.getInstance().getReference().child("Friend_List");
                    friendList.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendList.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    send.setEnabled(true);
                                    current_state = "not_friends";
                                    send.setText("Send Friend Request ");
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}
