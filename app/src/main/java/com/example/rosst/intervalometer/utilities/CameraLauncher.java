package com.example.rosst.intervalometer.utilities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.floatingButtonService.FloatingViewService;

public class CameraLauncher extends Activity {
    private static String cameraNamePref;
    private static String cameraPackagePref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSharedPrefs();
        cameraLauncher(this);
        finish();
    }

    public static void cameraLauncher(Context context) {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final Intent floatingButton = new Intent(context, FloatingViewService.class);
        try {
            PackageManager pm = context.getPackageManager();

            final ResolveInfo mInfo = pm.resolveActivity(i, 0);

            System.out.println("Chosen App: context " + context);
            System.out.println("Chosen App: name " + cameraNamePref + " pref " + cameraPackagePref);

            Intent intent = new Intent();
            if (!cameraNamePref.equals("Camera") &&
                            !cameraPackagePref.equals("CameraPackage")) {
                intent.setComponent(new ComponentName(cameraPackagePref, cameraNamePref));
            } else {
                intent.setComponent(new ComponentName(
                        mInfo.activityInfo.packageName,
                        mInfo.activityInfo.name));
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
            }
            context.startService(floatingButton);
            context.startActivity(intent);
        } catch (Exception e) {
            System.out.println("MainActivity camera Exception " + e);
        }
    }

    public void getSharedPrefs(){
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        cameraNamePref = getResources().getString(R.string.camera_app_name);
        cameraPackagePref = getResources().getString(R.string.camera_app_package);

        cameraNamePref = sharedPref.getString(getString(R.string.camera_app_name),
                getResources().getString(R.string.camera_app_name_default));
        cameraPackagePref = sharedPref.getString(getString(R.string.camera_app_package),
                getResources().getString(R.string.camera_app_package_default));
    }
}
