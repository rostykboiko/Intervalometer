package com.example.rosst.intervalometer.intervalometer;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.view.KeyEvent;

import com.example.rosst.intervalometer.main.MainActivity;
import com.example.rosst.intervalometer.utilities.AccessibilityUtils;
import com.example.rosst.intervalometer.utilities.Callback;

import java.util.TimerTask;

public class IntervalometerTask extends TimerTask {
    private int currentFrame = 1;
    private int numOfFrames;
    private AccessibilityUtils accessibilityService;
    private Callback callback;

    public void registerCallBack(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (currentFrame < numOfFrames) {
//                        Instrumentation inst = new Instrumentation();
//                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);
                        accessibilityService = new AccessibilityUtils();
                        accessibilityService.triggerShutter();
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

    public void setNumOfFrames(int numOfFrames) {
        this.numOfFrames = numOfFrames;
    }
}