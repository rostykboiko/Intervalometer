package com.example.rosst.intervalometer.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.floatingButtonService.FloatingViewService;
import com.example.rosst.intervalometer.floatingButtonService.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    public static Handler UIHandler;

    static {UIHandler = new Handler(Looper.getMainLooper());}

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    @BindView(R.id.on_switch)
    Switch onSwitch;

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
        final Intent floatingButton = new Intent(MainActivity.this, FloatingViewService.class);

        onSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSwitch.isChecked()) {
                    startService(floatingButton);
                    finish();
                } else {
                    stopService(floatingButton);
                }
            }
        });
    }
}