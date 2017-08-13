package com.example.rosst.intervalometer.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.service.FloatingViewService;

public class MainActivity extends AppCompatActivity  {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private Switch onSwitch;
    private RelativeLayout switchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onSwitch = (Switch) findViewById(R.id.on_switch);
        switchLayout = (RelativeLayout) findViewById(R.id.on_layout);

        switchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSwitch.isChecked()) {
                    onSwitch.setChecked(false);
                }
                else {
                    onSwitch.setChecked(true);
                    finish();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
        if (FloatingViewService.isViewVisible()){
            onSwitch.setChecked(true);
        } else {
            onSwitch.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Set and initialize the view elements.
     */
    private void initializeView() {
        final Intent floatingButton = new Intent(MainActivity.this, FloatingViewService.class);

        onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    startService(floatingButton);
                } else {
                    stopService(floatingButton);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}