package com.example.tanishqsaluja.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tanishqsaluja on 11/2/18.
 */

public class SettingsActivity extends AppCompatActivity {
    TextView displayName,displayEmail,displayStatus;
    Button thumb,change_status;
    CircleImageView circleImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_settings);
        displayName = findViewById(R.id.name);
        displayEmail =findViewById(R.id.email);
        displayStatus =findViewById(R.id.status);
        thumb=findViewById(R.id.changephoto);
        change_status=findViewById(R.id.changestatus);
        circleImageView=findViewById(R.id.profile_image);
        //First grab that user frm database
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        //You can retrieve data using addEventListener or addChildeventlistener
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue(String.class);
                String email=dataSnapshot.child("email").getValue(String.class);
                //String status=dataSnapshot.child("status").getValue(String.class);
                String thumbnail=dataSnapshot.child("thumbnail").getValue(String.class);
                String photo=dataSnapshot.child("photo").getValue(String.class);


                //For the first time when we run the other data loads
                //So we want to use the offline feature of firebase HERE

                displayName.setText(name);
                displayEmail.setText(email);
                //displayStatus.setText(status);

                //circleImageView.setImageResource();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
