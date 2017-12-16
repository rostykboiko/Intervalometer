package com.example.rosst.intervalometer.quickTitle;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

import com.example.rosst.intervalometer.utilities.CameraLauncher;

@TargetApi(Build.VERSION_CODES.N)
public class QSIntentService extends TileService {

    @Override
    public void onClick() {
        boolean isCurrentlyLocked = this.isLocked();


        if (!isCurrentlyLocked) {
            startActivity(new Intent(QSIntentService.this, CameraLauncher.class));
        }
    }
}
