package com.example.rosst.intervalometer.horizontalWheel;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.AbsSeekBar;

public class SeekBarView extends AbsSeekBar{

    public SeekBarView(Context context) {
        super(context);
    }

    public SeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SeekBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
