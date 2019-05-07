package com.banksaku.banksaku;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Banksaku extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Offline Mode
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
