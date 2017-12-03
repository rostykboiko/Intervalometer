package com.example.rosst.intervalometer.service;

import com.example.rosst.intervalometer.main.MainActivity;

import java.util.TimerTask;

public class DurationTask extends TimerTask {
    private int duration = 0;

    private Callback callback;

    void registerCallBack(Callback callback) {
        this.callback = callback;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(500);
            duration++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        MainActivity.runOnUI(new Runnable() {
            public void run() {
                if (callback != null) {
                    callback.callBackDuration(duration);
                }
            }
        });
    }

    interface Callback {
        void callBackDuration(int duration);
    }
}
