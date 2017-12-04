package com.example.rosst.intervalometer.service;

import android.app.Instrumentation;
import android.view.KeyEvent;

import com.example.rosst.intervalometer.main.MainActivity;
import com.example.rosst.intervalometer.utilities.Callback;

import java.util.TimerTask;

public class IntervalometerTask extends TimerTask {
    private int currentFrame = 1;
    private int numOfFrames;

    private Callback callback;

    void registerCallBack(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (currentFrame < numOfFrames) {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);
                        Thread.sleep(200);
                        currentFrame++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
        MainActivity.runOnUI(new Runnable() {
            public void run() {
                if (callback != null) {
                    callback.callBackFrames(currentFrame, numOfFrames);
                }
            }
        });
    }

    void setNumOfFrames(int numOfFrames) {
        this.numOfFrames = numOfFrames;
    }
}