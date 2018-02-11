package com.example.tanishqsaluja.chatapp;

import android.app.ProgressDialog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyStatusAct extends AppCompatActivity {
    Toolbar toolbar;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_status);
        toolbar = findViewById(R.id.statusbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Status");
//        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(toolbar);
        Button button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        editText.setText(getIntent().getStringExtra("earlier_status"));
        final ProgressDialog progressDialog = new ProgressDialog(this);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String id = firebaseUser.getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Changing status");
                progressDialog.setMessage("Please wait while status is being updated.");
                progressDialog.show();
                //Always use oncomplete listener for checking completion of such tasks
                String newStatus = editText.getText().toString();
                Log.e("test",newStatus);
                databaseReference.child("status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(MyStatusAct.this, "Error while changing", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }
}
