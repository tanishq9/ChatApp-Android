package com.example.tanishqsaluja.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class SigninActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    android.support.v7.widget.Toolbar mybar;
    EditText displayName,email,password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

        mybar=findViewById(R.id.signtoolbar);
        setSupportActionBar(mybar);
        getSupportActionBar().setTitle("SIGN IN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        Button button=findViewById(R.id.signbutton);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        progressDialog=new ProgressDialog(SigninActivity.this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailaddress=email.getText().toString();
                String pass=password.getText().toString();
                if(TextUtils.isEmpty(emailaddress) || TextUtils.isEmpty(pass)){
                    Toast.makeText(SigninActivity.this,"Invalid Details",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Checking your credentails");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(emailaddress,pass)
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //Sign in success
                                        progressDialog.dismiss();
                                        Intent startIntent=new Intent(SigninActivity.this,MainActivity.class);
                                        startActivity(startIntent);
                                        finish();
                                    }
                                    else{
                                        progressDialog.hide();
                                        Toast.makeText(SigninActivity.this,"User doesn't exist",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }


}
