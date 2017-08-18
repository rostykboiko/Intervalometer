package com.example.rosst.intervalometer.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.service.FloatingViewService;
import com.example.rosst.intervalometer.service.IntervalometerTask;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

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