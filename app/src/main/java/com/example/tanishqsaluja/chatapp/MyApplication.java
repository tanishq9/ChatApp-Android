package com.example.tanishqsaluja.chatapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tanishqsaluja on 12/2/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

