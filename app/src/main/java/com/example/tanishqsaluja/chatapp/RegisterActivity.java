package com.example.tanishqsaluja.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by tanishqsaluja on 10/2/18.
 */

public class RegisterActivity extends AppCompatActivity {
    EditText displayName,email,password;
    Button button;
    android.support.v7.widget.Toolbar mybar;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        mybar=findViewById(R.id.registertoolbar);
        setSupportActionBar(mybar);
        getSupportActionBar().setTitle("REGISTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayName=findViewById(R.id.displayName);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        button=findViewById(R.id.button);
        progressDialog=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailaddress=email.getText().toString();
                String pass=password.getText().toString();
                String name=displayName.getText().toString();
                if(TextUtils.isEmpty(emailaddress) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this,"Invalid Details",Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please wait while we create your account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(emailaddress, pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Log.e("TEST", "LOGGING IN");
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}