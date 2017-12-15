package com.example.rosst.intervalometer.horizontalWheel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.rosst.intervalometer.utilities.*;

import java.util.Arrays;

class Drawer {
    private static final int DP_CURSOR_CORNERS_RADIUS = 1;
    private static final int DP_NORMAL_MARK_WIDTH = 1;
    private static final int DP_ZERO_MARK_WIDTH = 2;
    private static final int DP_CURSOR_WIDTH = 3;
    private static final float NORMAL_MARK_RELATIVE_HEIGHT = 0.6F;
    private static final float ZERO_MARK_RELATIVE_HEIGHT = 0.8F;
    private static final float CURSOR_RELATIVE_HEIGHT = 1.0F;
    private static final float SHADE_RANGE = 0.7F;
    private static final float SCALE_RANGE = 0.1F;
    private Paint paint = new Paint(1);
    private HorizontalWheelView view;
    private int marksCount;
    private int normalColor;
    private int activeColor;
    private boolean showActiveRange;
    private float[] gaps;
    private float[] shades;
    private float[] scales;
    private int[] colorSwitches = new int[]{-1, -1, -1};
    private int viewportHeight;
    private int normalMarkWidth;
    private int normalMarkHeight;
    private int zeroMarkWidth;
    private int zeroMarkHeight;
    private int cursorCornersRadius;
    private RectF cursorRect = new RectF();
    private int maxVisibleMarksCount;

    Drawer(HorizontalWheelView view) {
        this.view = view;
        this.initDpSizes();
    }

    private void initDpSizes() {
        this.normalMarkWidth = this.convertToPx(1);
        this.zeroMarkWidth = this.convertToPx(2);
        this.cursorCornersRadius = this.convertToPx(1);
    }

    private int convertToPx(int dp) {
        return Utils.convertToPx(dp, this.view.getResources());
    }

    void setMarksCount(int marksCount) {
        this.marksCount = marksCount;
        this.maxVisibleMarksCount = marksCount / 2 + 1;
        this.gaps = new float[this.maxVisibleMarksCount];
        this.shades = new float[this.maxVisibleMarksCount];
        this.scales = new float[this.maxVisibleMarksCount];
    }

    void setNormalColor(int color) {
        this.normalColor = color;
    }

    void setActiveColor(int color) {
        this.activeColor = color;
    }

    void setShowActiveRange(boolean show) {
        this.showActiveRange = show;
    }

    void onSizeChanged() {
        this.viewportHeight = this.view.getHeight() - this.view.getPaddingTop() - this.view.getPaddingBottom();
        this.normalMarkHeight = (int)((float)this.viewportHeight * 0.6F);
        this.zeroMarkHeight = (int)((float)this.viewportHeight * 0.8F);
        this.setupCursorRect();
    }

    private void setupCursorRect() {
        int cursorHeight = (int)((float)this.viewportHeight * 1.0F);
        this.cursorRect.top = (float)(this.view.getPaddingTop() + (this.viewportHeight - cursorHeight) / 2);
        this.cursorRect.bottom = this.cursorRect.top + (float)cursorHeight;
        int cursorWidth = this.convertToPx(3);
        this.cursorRect.left = (float)((this.view.getWidth() - cursorWidth) / 2);
        this.cursorRect.right = this.cursorRect.left + (float)cursorWidth;
    }

    int getMarksCount() {
        return this.marksCount;
    }

    void onDraw(Canvas canvas) {
        double step = 6.283185307179586D / (double)this.marksCount;
        double offset = (1.5707963267948966D - this.view.getRadiansAngle()) % step;
        if(offset < 0.0D) {
            offset += step;
        }

        this.setupGaps(step, offset);
        this.setupShadesAndScales(step, offset);
        int zeroIndex = this.calcZeroIndex(step);
        this.setupColorSwitches(step, offset, zeroIndex);
        this.drawMarks(canvas, zeroIndex);
        this.drawCursor(canvas);
    }

    private void setupGaps(double step, double offset) {
        this.gaps[0] = (float)Math.sin(offset / 2.0D);
        float sum = this.gaps[0];
        double angle = offset;

        int n;
        for(n = 1; angle + step <= 3.141592653589793D; ++n) {
            this.gaps[n] = (float)Math.sin(angle + step / 2.0D);
            sum += this.gaps[n];
            angle += step;
        }

        float lastGap = (float)Math.sin((3.141592653589793D + angle) / 2.0D);
        sum += lastGap;
        if(n != this.gaps.length) {
            this.gaps[this.gaps.length - 1] = -1.0F;
        }

        float k = (float)this.view.getWidth() / sum;

        for(int i = 0; i < this.gaps.length; ++i) {
            if(this.gaps[i] != -1.0F) {
                this.gaps[i] *= k;
            }
        }

    }

    private void setupShadesAndScales(double step, double offset) {
        double angle = offset;

        for(int i = 0; i < this.maxVisibleMarksCount; ++i) {
            double sin = Math.sin(angle);
            this.shades[i] = (float)(1.0D - 0.699999988079071D * (1.0D - sin));
            this.scales[i] = (float)(1.0D - 0.10000000149011612D * (1.0D - sin));
            angle += step;
        }

    }

    private int calcZeroIndex(double step) {
        double twoPi = 6.283185307179586D;
        double normalizedAngle = (this.view.getRadiansAngle() + 1.5707963267948966D + twoPi) % twoPi;
        return normalizedAngle > 3.141592653589793D?-1:(int)((3.141592653589793D - normalizedAngle) / step);
    }

    private void setupColorSwitches(double step, double offset, int zeroIndex) {
        if(!this.showActiveRange) {
            Arrays.fill(this.colorSwitches, -1);
        } else {
            double angle = this.view.getRadiansAngle();
            int afterMiddleIndex = 0;
            if(offset < 1.5707963267948966D) {
                afterMiddleIndex = (int)((1.5707963267948966D - offset) / step) + 1;
            }

            if(angle > 4.71238898038469D) {
                this.colorSwitches[0] = 0;
                this.colorSwitches[1] = afterMiddleIndex;
                this.colorSwitches[2] = zeroIndex;
            } else if(angle >= 0.0D) {
                this.colorSwitches[0] = Math.max(0, zeroIndex);
                this.colorSwitches[1] = afterMiddleIndex;
                this.colorSwitches[2] = -1;
            } else if(angle < -4.71238898038469D) {
                this.colorSwitches[0] = 0;
                this.colorSwitches[1] = zeroIndex;
                this.colorSwitches[2] = afterMiddleIndex;
            } else if(angle < 0.0D) {
                this.colorSwitches[0] = afterMiddleIndex;
                this.colorSwitches[1] = zeroIndex;
                this.colorSwitches[2] = -1;
            }

        }
    }

    private void drawMarks(Canvas canvas, int zeroIndex) {
        float x = (float)this.view.getPaddingLeft();
        int color = this.normalColor;
        int colorPointer = 0;

        for(int i = 0; i < this.gaps.length && this.gaps[i] != -1.0F; ++i) {
            for(x += this.gaps[i]; colorPointer < 3 && i == this.colorSwitches[colorPointer]; ++colorPointer) {
                color = color == this.normalColor?this.activeColor:this.normalColor;
            }

            if(i != zeroIndex) {
                this.drawNormalMark(canvas, x, this.scales[i], this.shades[i], color);
            } else {
                this.drawZeroMark(canvas, x, this.scales[i], this.shades[i]);
            }
        }

    }

    private void drawNormalMark(Canvas canvas, float x, float scale, float shade, int color) {
        float height = (float)this.normalMarkHeight * scale;
        float top = (float)this.view.getPaddingTop() + ((float)this.viewportHeight - height) / 2.0F;
        float bottom = top + height;
        this.paint.setStrokeWidth((float)this.normalMarkWidth);
        this.paint.setColor(this.applyShade(color, shade));
        canvas.drawLine(x, top, x, bottom, this.paint);
    }

    private int applyShade(int color, float shade) {
        int r = (int)((float)Color.red(color) * shade);
        int g = (int)((float)Color.green(color) * shade);
        int b = (int)((float)Color.blue(color) * shade);
        return Color.rgb(r, g, b);
    }

    private void drawZeroMark(Canvas canvas, float x, float scale, float shade) {
        float height = (float)this.zeroMarkHeight * scale;
        float top = (float)this.view.getPaddingTop() + ((float)this.viewportHeight - height) / 2.0F;
        float bottom = top + height;
        this.paint.setStrokeWidth((float)this.zeroMarkWidth);
        this.paint.setColor(this.applyShade(this.activeColor, shade));
        canvas.drawLine(x, top, x, bottom, this.paint);
    }

    private void drawCursor(Canvas canvas) {
        this.paint.setStrokeWidth(0.0F);
        this.paint.setColor(this.activeColor);
        canvas.drawRoundRect(this.cursorRect, (float)this.cursorCornersRadius, (float)this.cursorCornersRadius, this.paint);
    }
}
