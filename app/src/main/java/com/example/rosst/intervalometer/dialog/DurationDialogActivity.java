package com.example.rosst.intervalometer.dialog;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.service.FloatingViewService;


import butterknife.BindView;
import butterknife.ButterKnife;

public class DurationDialogActivity extends AppCompatActivity {
    private String framesString = "0";

    @BindView(R.id.num_of_frames)
    TextView numOfFrames;
    @BindView(R.id.button_1)
    TextView button1;
    @BindView(R.id.button_2)
    TextView button2;
    @BindView(R.id.button_3)
    TextView button3;
    @BindView(R.id.button_4)
    TextView button4;
    @BindView(R.id.button_5)
    TextView button5;
    @BindView(R.id.button_6)
    TextView button6;
    @BindView(R.id.button_7)
    TextView button7;
    @BindView(R.id.button_8)
    TextView button8;
    @BindView(R.id.button_9)
    TextView button9;
    @BindView(R.id.button_0)
    TextView button0;
    @BindView(R.id.button_cancel)
    TextView buttonCancel;
    @BindView(R.id.button_save)
    TextView buttonSave;
    @BindView(R.id.deleteBtn)
    ImageView deleteButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_duration);

        ButterKnife.bind(this);

        setStatusBarDim(true);
        initClickListeners();
    }


    private void setStatusBarDim(boolean dim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(dim ? Color.TRANSPARENT :
                    ContextCompat.getColor(this, getThemedResId(R.attr.colorPrimaryDark)));
        }
    }

    private int getThemedResId(@AttrRes int attr) {
        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[]{attr});
        int resId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        return resId;
    }

    private void setFrameCount(int buttonNumber) {
        if (framesString.length() < 4) {
            if (!framesString.equals("0")) {
                framesString += buttonNumber + "";
                numOfFrames.setText(framesString);
                deleteButton.setImageDrawable(getDrawable(R.drawable.ic_material_backspace_white));
            } else if (buttonNumber != 0) {
                framesString = buttonNumber + "";
                numOfFrames.setText(framesString);
                deleteButton.setImageDrawable(getDrawable(R.drawable.ic_material_backspace_white));
            }
        }
    }

    private String deleteTime(String str) {
        if (str != null && str.length() > 1) {
            str = str.substring(0, str.length() - 1);
        } else {
            str = "0";
            numOfFrames.setText(str);
            deleteButton.setImageDrawable(getDrawable(R.drawable.ic_material_backspace_white03));
        }
        return str;
    }

    void initClickListeners() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(2);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(3);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(4);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(5);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(6);
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(7);
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(8);
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(9);
            }
        });
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrameCount(0);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final Intent floatingButton = new
                            Intent(DurationDialogActivity.this, FloatingViewService.class);
                    floatingButton.putExtra("Custom Frames", numOfFrames.getText().toString());
                    startService(floatingButton);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTime(framesString = deleteTime(framesString));
                numOfFrames.setText(framesString);
            }
        });
        deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numOfFrames.setText(R.string.time_seconds_00);
                return true;
            }
        });
    }
}