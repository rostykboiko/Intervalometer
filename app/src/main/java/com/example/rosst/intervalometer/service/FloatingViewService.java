package com.example.rosst.intervalometer.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.main.MainActivity;
import com.github.shchurov.horizontalwheelview.HorizontalWheelView;

import java.util.Timer;

public class FloatingViewService extends Service{
    private int delay = 1000;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private TextView shutterValue;
    private HorizontalWheelView horizontalWheelView;
    private Timer mTimer = new Timer();
    private TextView duration;
    private IntervalometerTask intervalometerTask = new IntervalometerTask();


    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFloatingView();

        horizontalWheelView = (HorizontalWheelView) mFloatingView.findViewById(R.id.shutter_wheel);
        duration = (TextView) mFloatingView.findViewById(R.id.duration_counter_tv);

        getIntervalValue();
        initButtons();
        viewOn();
        setupListeners();
        updateUi();
        initSpinnerMenus();
    }

    private void initButtons() {
        TextView openButton = (TextView) mFloatingView.findViewById(R.id.info_tv);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intervalometerTask.cancel();

                startActivity(intent);

                stopSelf();
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

    private void initFloatingView() {
        mFloatingView = LayoutInflater.from(FloatingViewService.this).inflate(R.layout.layout_floating_widget, null);

        shutterValue = (TextView) mFloatingView.findViewById(R.id.shutter_value);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        //Set the close button
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
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

    private void startIntervalometer(int delay, int intervalValue) {
        mFloatingView.findViewById(R.id.controls_container).setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.expanded_container).setVisibility(View.GONE);
        mTimer.schedule(intervalometerTask, delay, intervalValue);
    }

    private void stopIntervalometer() {
        intervalometerTask.cancel();
        intervalometerTask = new IntervalometerTask();

        mFloatingView.findViewById(R.id.controls_container).setVisibility(View.GONE);
        mFloatingView.findViewById(R.id.expanded_container).setVisibility(View.VISIBLE);
    }

    private void initSpinnerMenus() {
        Spinner spinnerShutter = (Spinner) mFloatingView.findViewById(R.id.spinner_shutter_btn);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterShutter = ArrayAdapter.createFromResource(this,
                R.array.shutter_buttons_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterShutter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerShutter.setAdapter(adapterShutter);

        Spinner spinnerDuration = (Spinner) mFloatingView.findViewById(R.id.spinner_duration);
        ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(this,
                R.array.duration_array, android.R.layout.simple_spinner_item);
        adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(adapterDuration);

        Spinner spinnerIntervals = (Spinner) mFloatingView.findViewById(R.id.spinner_intervals);
        ArrayAdapter<CharSequence> adapterIntervals = ArrayAdapter.createFromResource(this,
                R.array.intervals_array, android.R.layout.simple_spinner_item);
        adapterIntervals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIntervals.setAdapter(adapterIntervals);

        Spinner spinnerFrameRate = (Spinner) mFloatingView.findViewById(R.id.spinner_frames);
        ArrayAdapter<CharSequence> adapterFrameRate = ArrayAdapter.createFromResource(this,
                R.array.frame_rates_array, android.R.layout.simple_spinner_item);
        adapterFrameRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrameRate.setAdapter(adapterFrameRate);
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
}
