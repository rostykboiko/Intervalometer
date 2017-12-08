package com.example.rosst.intervalometer.utilities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.rosst.intervalometer.floatingButtonService.FloatingViewService;

public class CameraLauncher extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraLauncher(this);
        finish();
    }

    public static void cameraLauncher(Context context) {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final Intent floatingButton = new Intent(context, FloatingViewService.class);

        try {
            PackageManager pm = context.getPackageManager();

            final ResolveInfo mInfo = pm.resolveActivity(i, 0);

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            context.startService(floatingButton);
            context.startActivity(intent);
        } catch (Exception e) {
            System.out.println("MainActivity camera Exception " + e);
        }
    }

}
