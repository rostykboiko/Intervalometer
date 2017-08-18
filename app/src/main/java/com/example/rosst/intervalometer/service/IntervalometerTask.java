package com.example.rosst.intervalometer.service;

import android.app.Instrumentation;
import android.view.KeyEvent;

import com.example.rosst.intervalometer.main.MainActivity;

import java.util.TimerTask;

public class IntervalometerTask extends TimerTask {
    private String framesCounter;
    int i = 0;

    @Override
    public void run() {
        framesCounter = "00:00:00";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (i <120){
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);
                    Thread.sleep(200);
                    i++;
                    }
                } catch (InterruptedException e) {}
            }
        }).start();

        MainActivity.runOnUI(new Runnable() {
            public void run() {

                System.out.println("Hi, again!");
            }
        });
    }
}