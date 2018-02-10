package com.example.tanishqsaluja.chatapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    android.support.v7.widget.Toolbar mybar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager viewPager=findViewById(R.id.viewPager);
        TableLayout tableLayout=findViewById(R.id.tabLayout);
        //viewPager.setAdapter();


        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        mybar = findViewById(R.id.mytoolbar);
        setSupportActionBar(mybar);
        getSupportActionBar().setTitle("Chat");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser==null){
            sendToStart();
        }
    }

    public void sendToStart(){
        Intent startIntent=new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_layout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        return true;
    }
}

