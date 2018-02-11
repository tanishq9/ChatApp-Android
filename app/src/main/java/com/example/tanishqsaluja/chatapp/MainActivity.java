package com.example.tanishqsaluja.chatapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    android.support.v7.widget.Toolbar mybar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        mybar = findViewById(R.id.mytoolbar);
        setSupportActionBar(mybar);
        getSupportActionBar().setTitle("ChatApp");


        ViewPager viewPager=findViewById(R.id.viewPager);
        TabLayout tabLayout=findViewById(R.id.tabLayout);
        MyFragmentClass myFragmentClass=new MyFragmentClass(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentClass);
        tabLayout.setupWithViewPager(viewPager);

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
        else if(item.getItemId()==R.id.accountsettings){
            Intent settingIntent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingIntent);
        }
        else if(item.getItemId()==R.id.allusers){
            Intent userIntent=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(userIntent);
        }
        return true;
    }

    class MyFragmentClass extends FragmentPagerAdapter {
        public MyFragmentClass(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Requests";
                case 1: return "Chats";
                case 2: return "Friends";
            }
            return "Null";
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return new FragmentRequests();
            }
            else if(position==1){
                return new FragmentChats();
            }
            else if(position==2){
                return new FragmentFriends();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}

