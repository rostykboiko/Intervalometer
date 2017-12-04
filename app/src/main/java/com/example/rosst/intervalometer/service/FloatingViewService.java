package com.example.rosst.intervalometer.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.main.MainActivity;
import com.example.rosst.intervalometer.dialog.DurationDialogActivity;
import com.example.rosst.intervalometer.utilities.Callback;
import com.github.shchurov.horizontalwheelview.HorizontalWheelView;

import java.util.Timer;

public class FloatingViewService extends Service
        implements Callback{
    private int delay = 1000;
    private int numOfFrames;

    private View mFloatingView;
    private WindowManager mWindowManager;
    private HorizontalWheelView horizontalWheelView;

    private TextView shutterValue;
    private TextView durationTV;
    private TextView framesCounterTV;

    private Timer mTimer = new Timer();
    private DurationTask durationTask = new DurationTask();
    private IntervalometerTask intervalometerTask = new IntervalometerTask();

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        initFloatingView();

        horizontalWheelView = (HorizontalWheelView) mFloatingView.findViewById(R.id.shutter_wheel);
        durationTV = (TextView) mFloatingView.findViewById(R.id.duration_counter_tv);
        framesCounterTV = (TextView) mFloatingView.findViewById(R.id.frames_counter_tv);

        durationTask.registerCallBack(this);
        intervalometerTask.registerCallBack(this);

        viewOn();
        updateUi();
        initButtons();
        setupListeners();
        initSpinnerMenus();
        getIntervalValue();

    }
    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent != null && intent.getExtras() != null) {
            framesCounterTV.setText(getString(R.string.frame_counter_tv) + intent.getStringExtra("Custom Frames"));

            System.out.println("Floating intent " + intent.getStringExtra("Custom Frames"));
        }

        return Service.START_STICKY;
    }

    private void initButtons() {
        TextView openButton = (TextView) mFloatingView.findViewById(R.id.info_tv);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intervalometerTask.cancel();

                stopSelf();
                startActivity(intent);
            }
        });

        TextView startButton = (TextView) mFloatingView.findViewById(R.id.start_tv);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntervalometer(delay, (int) (getIntervalValue() * 1000));
            }
        });

        TextView stopButton = (TextView) mFloatingView.findViewById(R.id.stop_tv);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopIntervalometer();
            }
        });
    }

    @SuppressLint("InflateParams")
    private void initFloatingView() {
        mFloatingView = LayoutInflater.from(FloatingViewService.this)
                .inflate(R.layout.layout_floating_widget, null);

        shutterValue = (TextView) mFloatingView.findViewById(R.id.shutter_value);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 140;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert mWindowManager != null;
        mWindowManager.addView(mFloatingView, params);

        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView();
            }
        });

        ImageView collapseBtn = (ImageView) mFloatingView.findViewById(R.id.collapse_btn);
        collapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseView();
            }
        });

        mFloatingView.findViewById(R.id.collapse_view).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY - (int) (event.getRawY() - initialTouchY);

                        closeFloatingView(params);

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void closeView(){
        stopService(new Intent(getApplicationContext(), FloatingViewService.class));
    }

    private void collapseView(){
        mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.expanded_container).setVisibility(View.GONE);
    }

    private void closeFloatingView(WindowManager.LayoutParams params) {
        if ((params.x >= 500 && params.x <= 615) && (params.y >= 170 && params.y <= 230)) {
            // System.out.print("\nTrashCan position: x - " + params.x + "y - " +params.y);
          //  System.out.print("\nTrashCan position: true");
            stopSelf();
        } else {
          //  System.out.print("\nTrashCan position: false");
        }
    }

    private double getIntervalValue() {
        double mAngle;
        int angle = (int) horizontalWheelView.getDegreesAngle();

        if (angle == 0) {
            mAngle = 0.250; // 1/4
        } else if (angle == 1.0) {
            mAngle = 0.5; // 1/2
        } else {
            mAngle = (angle - 1);
        }
        return mAngle;
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view)
                .getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        viewOff();
    }

    @SuppressLint("SetTextI18n")
    private void startIntervalometer(int delay, int intervalValue) {
        intervalometerTask = new IntervalometerTask();
        intervalometerTask.registerCallBack(this);
        framesCounterTV.setText(0 + "/" + numOfFrames);

        durationTask = new DurationTask();
        durationTask.registerCallBack(this);
        durationTV.setText(R.string.TimerDummy);

        mFloatingView.findViewById(R.id.controls_container).setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.expanded_container).setVisibility(View.GONE);
        intervalometerTask.setNumOfFrames(numOfFrames);

        mTimer.schedule(durationTask, 0, 1000);
        mTimer.schedule(intervalometerTask, delay, intervalValue);
    }

    private void stopIntervalometer() {
        intervalometerTask.cancel();
        durationTask.cancel();

        mFloatingView.findViewById(R.id.controls_container).setVisibility(View.GONE);
        mFloatingView.findViewById(R.id.expanded_container).setVisibility(View.VISIBLE);
    }

    private void initSpinnerMenus() {
        ArrayAdapter<CharSequence> adapterShutter = ArrayAdapter.createFromResource(this,
                R.array.shutter_buttons_array, android.R.layout.simple_spinner_item);
        adapterShutter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinnerFrameRate = (Spinner) mFloatingView.findViewById(R.id.spinner_intervals);
        ArrayAdapter<CharSequence> adapterFrameRate = ArrayAdapter.createFromResource(this,
                R.array.intervals_array, android.R.layout.simple_spinner_item);
        adapterFrameRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrameRate.setAdapter(adapterFrameRate);
        spinnerFrameRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String[] option = getResources().getStringArray(R.array.intervals_array);
                if (selectedItemPosition == 4) {
                    startActivity(new Intent(FloatingViewService.this,
                            DurationDialogActivity.class));
                    option[4] = "";
                    collapseView();
                } else {
                    numOfFrames = Integer.parseInt(option[selectedItemPosition]);
                    framesCounterTV.setText(0 + "/" + numOfFrames);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        adapterFrameRate.notifyDataSetChanged();
    }

    private void setupListeners() {
        horizontalWheelView.setListener(new HorizontalWheelView.Listener() {
            @Override
            public void onRotationChanged(double radians) {
                updateUi();
            }
        });
    }

    private void updateUi() {
        updateText();
    }

    @SuppressLint("SetTextI18n")
    private void updateText() {
        String sec = " sec";
        if (getIntervalValue() < 1)
            shutterValue.setText(getIntervalValue() + sec);
        else shutterValue.setText((int) getIntervalValue() + sec);
    }

    public static boolean isViewVisible() {
        return viewVisible;
    }

    public static void viewOn() {
        viewVisible = true;
    }

    public static void viewOff() {
        viewVisible = false;
    }

    private static boolean viewVisible;

    @SuppressLint("SetTextI18n")
    @Override
    public void callBackFrames(int currentNum, int numOfFrames) {
        framesCounterTV.setText(currentNum + "/" + numOfFrames);
    }

    @Override
    public void callBackDuration(int duration) {
        int sec, min, h;
        String durationStr;

        h = duration / 3600;
        min = duration / 60 % 60;
        sec = duration % 60;

        durationStr = h + ":";
        if (min < 10) durationStr += "0" + min;
        else durationStr += min + ":";
        if (sec < 10) durationStr += ":0" + sec;
        else durationStr += sec;

        durationTV.setText(durationStr);
    }
}
