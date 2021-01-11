package com.example.gonavigator.models;

import android.app.Application;
import android.os.SystemClock;

public class splash extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Tiempo de duración del splash
        SystemClock.sleep(3000);
    }
}
