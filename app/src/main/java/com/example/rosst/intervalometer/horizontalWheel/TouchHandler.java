package com.example.rosst.intervalometer.horizontalWheel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.example.rosst.intervalometer.horizontalWheel.HorizontalWheelView.Listener;

class TouchHandler extends SimpleOnGestureListener {
    private static final float SCROLL_ANGLE_MULTIPLIER = 0.002F;
    private static final float FLING_ANGLE_MULTIPLIER = 2.0E-4F;
    private static final int SETTLING_DURATION_MULTIPLIER = 1000;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator(2.5F);
    private HorizontalWheelView view;
    private Listener listener;
    private GestureDetector gestureDetector;
    private ValueAnimator settlingAnimator;
    private boolean snapToMarks;
    private int scrollState = 0;
    private AnimatorUpdateListener flingAnimatorListener = new AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            TouchHandler.this.view.setRadiansAngle((double)((Float)animation.getAnimatedValue()).floatValue());
        }
    };
    private AnimatorListener animatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            TouchHandler.this.updateScrollStateIfRequired(0);
        }
    };

    TouchHandler(HorizontalWheelView view) {
        this.view = view;
        this.gestureDetector = new GestureDetector(view.getContext(), this);
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    void setSnapToMarks(boolean snapToMarks) {
        this.snapToMarks = snapToMarks;
    }

    boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        int action = event.getActionMasked();
        if(this.scrollState != 2 && (action == 1 || action == 3)) {
            if (this.snapToMarks) {
                this.playSettlingAnimation(this.findNearestMarkAngle(this.view.getRadiansAngle()));
            } else {
                this.updateScrollStateIfRequired(0);
            }
        }
        return true;
    }

    public boolean onDown(MotionEvent e) {
        this.cancelFling();
        return true;
    }

    void cancelFling() {
        if(this.scrollState == 2) {
            this.settlingAnimator.cancel();
        }
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        double newAngle = this.view.getRadiansAngle() + (double)(distanceX * 0.002F);
        this.view.setRadiansAngle(newAngle);
        this.updateScrollStateIfRequired(1);
        return true;
    }

    private void updateScrollStateIfRequired(int newState) {
        if(this.listener != null && this.scrollState != newState) {
            this.scrollState = newState;
            this.listener.onScrollStateChanged(newState);
        }
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        double endAngle = this.view.getRadiansAngle() - (double)(velocityX * 2.0E-4F);
        if(this.snapToMarks) {
            endAngle = (double)((float)this.findNearestMarkAngle(endAngle));
        }

        this.playSettlingAnimation(endAngle);
        return true;
    }

    private double findNearestMarkAngle(double angle) {
        double step = 6.283185307179586D / (double)this.view.getMarksCount();
        return (double)Math.round(angle / step) * step;
    }

    private void playSettlingAnimation(double endAngle) {
        this.updateScrollStateIfRequired(2);
        double startAngle = this.view.getRadiansAngle();
        int duration = (int)(Math.abs(startAngle - endAngle) * 1000.0D);
        this.settlingAnimator = ValueAnimator.ofFloat(new float[]{(float)startAngle, (float)endAngle}).setDuration((long)duration);
        this.settlingAnimator.setInterpolator(INTERPOLATOR);
        this.settlingAnimator.addUpdateListener(this.flingAnimatorListener);
        this.settlingAnimator.addListener(this.animatorListener);
        this.settlingAnimator.start();
    }
}
