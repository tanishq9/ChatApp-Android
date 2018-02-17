package com.example.tanishqsaluja.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    StorageReference storageReference;

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

        storageReference = FirebaseStorage.getInstance().getReference();

        recyclerView = findViewById(R.id.rvmessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(ChatActivity.this, arrayList);
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


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image*s");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallaryIntent, "SELECT IMAGE"), 123);
             */
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);
            }
        });

        //function to retreive the messages

        loadmessages();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final String current_user_ref = "/messages/" + currentUserID + "/" + friendUserID + "/";
                final String friend_user = "/messages/" + friendUserID + "/" + currentUserID + "/";
                DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference()
                        .child(current_user_ref).child(friend_user).push();
                final String push_id = user_message_push.getKey();

                StorageReference filepath = storageReference.child("message_images").child(push_id + ".jpg");
                Uri resultUri = result.getUri();

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        String download_url = task.getResult().getDownloadUrl().toString();
                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("type", "image");
                        messageMap.put("from", friendUserID);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + push_id, messageMap);
                        messageUserMap.put(friend_user + push_id, messageMap);

                        entermessage.setText("");

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
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    /*    if (requestCode == 123 && requestCode == RESULT_OK) {
            Uri imageuri = data.getData();
            final String current_user_ref = "/messages/" + currentUserID + "/" + friendUserID + "/";
            final String friend_user = "/messages/" + friendUserID + "/" + currentUserID + "/";
            DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference()
                    .child(current_user_ref).child(friend_user).push();
            final String push_id = user_message_push.getKey();

            StorageReference filepath = storageReference.child("message_images").child(push_id + ".jpg");
            filepath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    String download_url = task.getResult().getDownloadUrl().toString();
                    Map messageMap = new HashMap();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("type", "image");
                    messageMap.put("from", friendUserID);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + push_id, messageMap);
                    messageUserMap.put(friend_user + push_id, messageMap);

                    entermessage.setText("");

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
            });
*/

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

