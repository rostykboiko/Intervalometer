package com.example.rosst.intervalometer.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.floatingButtonService.FloatingViewService;
import com.example.rosst.intervalometer.main.aboutDialog.AboutDialogFragment;
import com.example.rosst.intervalometer.main.appListDialog.PkgListFragment;
import com.example.rosst.intervalometer.utilities.CameraLauncher;
import com.example.rosst.intervalometer.utilities.RootCheckerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public static Handler UIHandler;
    private FragmentManager manager;
    private PkgListFragment pkgListFragment;
    private AboutDialogFragment aboutDialogFragment;
    private final Intent floatingButton = new Intent(MainActivity.this,
            FloatingViewService.class);

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
    @BindView(R.id.root_desc)
    TextView rootDesc;

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

        checkRootStatus();
        setCameraAppTitle();
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

    private void checkRootStatus() {
        if (!RootCheckerUtil.isDeviceRooted()) {
            rootDesc.setText(R.string.root_status_desc_err);
        }
    }

    @OnClick(R.id.on_layout)
    public void onSwitchRowClick() {
        if (onSwitch.isChecked()) {
            startActivity(new Intent(MainActivity.this, CameraLauncher.class));
            moveTaskToBack(true);
        } else {
            stopService(floatingButton);
        }
    }

    @OnClick(R.id.about)
    public void onAboutRowClick() {
        manager = getSupportFragmentManager();
        aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.show(manager, "dialog");
    }

    @OnClick(R.id.shortcut)
    public void createShortcutOfApp() {
        Intent shortcutIntent = new Intent(getApplicationContext(),
                CameraLauncher.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Intervals");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher_round));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        getApplicationContext().sendBroadcast(addIntent);
    }

    @OnClick(R.id.rootStatus)
    public void rootHintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.root_status_title);
        if (RootCheckerUtil.isDeviceRooted()) {
            builder.setMessage(R.string.root_status_desc);
        } else {
            builder.setMessage(R.string.root_dialog_text);
        }

        builder.setPositiveButton(R.string.root_dialog_button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initializeView() {
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
    public void selectCameraApp() {
        manager = getSupportFragmentManager();
        pkgListFragment = new PkgListFragment();
        pkgListFragment.show(manager, "dialog");
    }

    @Override
    public void onBackPressed() {
        if (pkgListFragment.isVisible()) {
            setCameraAppTitle();
            manager.beginTransaction().remove(pkgListFragment).commit();
        } else super.onBackPressed();
    }

    private void setCameraAppTitle() {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String cameraNamePref = sharedPref.getString(getString(R.string.camera_app_name),
                getResources().getString(R.string.camera_app_name_default));
        if (!cameraNamePref.equals("Camera"))
            cameraApp.setText(cameraNamePref);
    }
}