package com.example.rosst.intervalometer.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.floatingButtonService.FloatingViewService;
import com.example.rosst.intervalometer.utilities.CameraLauncher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public static Handler UIHandler;
    private FragmentManager manager;
    private PkgListFragment myDialogFragment;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    @BindView(R.id.on_switch)
    Switch onSwitch;
    @BindView(R.id.pkg_desc)
    TextView cameraApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
        setCameraAppTitle();
    }

    @OnClick(R.id.shortcut)
    public void createShortcutOfApp() {
        Intent shortcutIntent = new Intent(getApplicationContext(),
                CameraLauncher.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "App shortcut name");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher_round));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        getApplicationContext().sendBroadcast(addIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FloatingViewService.isViewVisible()) {
            onSwitch.setChecked(true);
        } else {
            onSwitch.setChecked(false);
        }
    }

    private void initializeView() {
        final Intent floatingButton = new Intent(MainActivity.this,
                FloatingViewService.class);
        onSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSwitch.isChecked()) {
                    startActivity(new Intent(MainActivity.this, CameraLauncher.class));
                    moveTaskToBack(true);
                } else {
                    stopService(floatingButton);
                }
            }
        });
    }

    @OnClick(R.id.appToLaunch)
    public void selectCameraApp(){
        manager = getSupportFragmentManager();
        myDialogFragment = new PkgListFragment();
        myDialogFragment.show(manager, "dialog");
    }

    @Override
    public void onBackPressed() {
        setCameraAppTitle();
        manager.beginTransaction().remove(myDialogFragment).commit();
    }

    private void setCameraAppTitle(){
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String cameraNamePref = sharedPref.getString(getString(R.string.camera_app_name),
                getResources().getString(R.string.camera_app_name_default));
        if (!cameraNamePref.equals("Camera"))
            cameraApp.setText(cameraNamePref);
    }
}