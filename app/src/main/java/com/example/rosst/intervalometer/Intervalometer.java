package com.example.rosst.intervalometer;

import android.app.Application;

public class Intervalometer extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
