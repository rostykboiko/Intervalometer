package com.example.rosst.intervalometer.service;

import android.app.Instrumentation;
import android.view.KeyEvent;

import java.util.TimerTask;

public class IntervalometerTask extends TimerTask {

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);
                    Thread.sleep(2000);
                }
                catch(InterruptedException e){

                }
            }
        }).start();
    }
}
