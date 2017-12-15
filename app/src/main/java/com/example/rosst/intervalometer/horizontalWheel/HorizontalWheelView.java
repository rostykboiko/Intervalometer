package com.example.rosst.intervalometer.horizontalWheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.rosst.intervalometer.R;
import com.example.rosst.intervalometer.utilities.Utils;

public class HorizontalWheelView extends View{
    private static final int DP_DEFAULT_WIDTH = 200;
    private static final int DP_DEFAULT_HEIGHT = 32;
    private static final int DEFAULT_MARKS_COUNT = 40;
    private static final int DEFAULT_NORMAL_COLOR = -1;
    private static final int DEFAULT_ACTIVE_COLOR = -11227920;
    private static final boolean DEFAULT_SHOW_ACTIVE_RANGE = true;
    private static final boolean DEFAULT_SNAP_TO_MARKS = false;
    private static final boolean DEFAULT_END_LOCK = false;
    private static final boolean DEFAULT_ONLY_POSITIVE_VALUES = false;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    private Drawer drawer = new Drawer(this);
    private TouchHandler touchHandler = new TouchHandler(this);
    private double angle;
    private boolean onlyPositiveValues;
    private boolean endLock;
    private HorizontalWheelView.Listener listener;

    public HorizontalWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.readAttrs(attrs);
    }

    private void readAttrs(AttributeSet attrs) {
        TypedArray a = this.getContext().obtainStyledAttributes(attrs,
                R.styleable.HorizontalWheelView);
        int marksCount = a.getInt(
                R.styleable.HorizontalWheelView_marksCount, 40);
        this.drawer.setMarksCount(marksCount);
        int normalColor = a.getColor(R.styleable.HorizontalWheelView_normalColor, -1);
        this.drawer.setNormalColor(normalColor);
        int activeColor = a.getColor(R.styleable.HorizontalWheelView_activeColor, -11227920);
        this.drawer.setActiveColor(activeColor);
        boolean showActiveRange = a.getBoolean(R.styleable.HorizontalWheelView_showActiveRange, true);
        this.drawer.setShowActiveRange(showActiveRange);
        boolean snapToMarks = a.getBoolean(R.styleable.HorizontalWheelView_snapToMarks, false);
        this.touchHandler.setSnapToMarks(snapToMarks);
        this.endLock = a.getBoolean(R.styleable.HorizontalWheelView_endLock, false);
        this.onlyPositiveValues = a.getBoolean(R.styleable.HorizontalWheelView_onlyPositiveValues, false);
        a.recycle();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        this.touchHandler.setListener(listener);
    }

    public void setRadiansAngle(double radians) {
        if(!this.checkEndLock(radians)) {
            this.angle = radians % 6.283185307179586D;
        }

        if(this.onlyPositiveValues && this.angle < 0.0D) {
            this.angle += 6.283185307179586D;
        }

        this.invalidate();
        if(this.listener != null) {
            this.listener.onRotationChanged(this.angle);
        }

    }

    private boolean checkEndLock(double radians) {
        if(!this.endLock) {
            return false;
        } else {
            boolean hit = false;
            if(radians >= 6.283185307179586D) {
                this.angle = Math.nextAfter(6.283185307179586D, -1.0D / 0.0);
                hit = true;
            } else if(this.onlyPositiveValues && radians < 0.0D) {
                this.angle = 0.0D;
                hit = true;
            } else if(radians <= -6.283185307179586D) {
                this.angle = Math.nextAfter(-6.283185307179586D, 1.0D / 0.0);
                hit = true;
            }

            if(hit) {
                this.touchHandler.cancelFling();
            }

            return hit;
        }
    }

    public void setDegreesAngle(double degrees) {
        double radians = degrees * 3.141592653589793D / 180.0D;
        this.setRadiansAngle(radians);
    }

    public void setCompleteTurnFraction(double fraction) {
        double radians = fraction * 2.0D * 3.141592653589793D;
        this.setRadiansAngle(radians);
    }

    public double getRadiansAngle() {
        return this.angle;
    }

    public double getDegreesAngle() {
        return this.getRadiansAngle() * 180.0D / 3.141592653589793D;
    }

    public double getCompleteTurnFraction() {
        return this.getRadiansAngle() / 6.283185307179586D;
    }

    public void setOnlyPositiveValues(boolean onlyPositiveValues) {
        this.onlyPositiveValues = onlyPositiveValues;
    }

    public void setEndLock(boolean lock) {
        this.endLock = lock;
    }

    public void setMarksCount(int marksCount) {
        this.drawer.setMarksCount(marksCount);
        this.invalidate();
    }

    public void setShowActiveRange(boolean show) {
        this.drawer.setShowActiveRange(show);
        this.invalidate();
    }

    public void setNormaColor(int color) {
        this.drawer.setNormalColor(color);
        this.invalidate();
    }

    public void setActiveColor(int color) {
        this.drawer.setActiveColor(color);
        this.invalidate();
    }

    public void setSnapToMarks(boolean snapToMarks) {
        this.touchHandler.setSnapToMarks(snapToMarks);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.touchHandler.onTouchEvent(event);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.drawer.onSizeChanged();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resolvedWidthSpec = this.resolveMeasureSpec(widthMeasureSpec, 200);
        int resolvedHeightSpec = this.resolveMeasureSpec(heightMeasureSpec, 32);
        super.onMeasure(resolvedWidthSpec, resolvedHeightSpec);
    }

    @SuppressLint("WrongConstant")
    private int resolveMeasureSpec(int measureSpec, int dpDefault) {
        int mode = MeasureSpec.getMode(measureSpec);
        if(mode == 1073741824) {
            return measureSpec;
        } else {
            int defaultSize = Utils.convertToPx(dpDefault, this.getResources());
            if(mode == -2147483648) {
                defaultSize = Math.min(defaultSize, MeasureSpec.getSize(measureSpec));
            }

            return MeasureSpec.makeMeasureSpec(defaultSize, 1073741824);
        }
    }

    protected void onDraw(Canvas canvas) {
        this.drawer.onDraw(canvas);
    }

    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.angle = this.angle;
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.angle = ss.angle;
        this.invalidate();
    }

    int getMarksCount() {
        return this.drawer.getMarksCount();
    }

    @SuppressWarnings("WeakerAccess")
    public static class Listener {
        public Listener() {
        }

        public void onRotationChanged(double radians) {
        }

        void onScrollStateChanged(int state) {
        }
    }
}

