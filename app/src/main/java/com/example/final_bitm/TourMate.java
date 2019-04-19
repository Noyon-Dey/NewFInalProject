package com.example.final_bitm;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Nizam Uddin Shamrat on 2/14/2018.
 */

public class TourMate extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
