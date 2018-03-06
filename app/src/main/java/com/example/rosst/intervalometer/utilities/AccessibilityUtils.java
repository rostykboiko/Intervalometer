package com.example.rosst.intervalometer.utilities;


import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.graphics.Path;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class AccessibilityUtils extends AccessibilityService {

    private static final String TAG = AccessibilityUtils.class
            .getSimpleName();

    public void moveCursor(){

    }

    public void performTouch(){

    }

    @TargetApi(24)
    public void triggerShutter() {
        Path localPath = new Path();
        localPath.moveTo(540, 1750);
        localPath.lineTo(540, 1750);
        GestureDescription.Builder localBuilder = new GestureDescription.Builder();
        localBuilder.addStroke(new GestureDescription.StrokeDescription(localPath, 100, 500));
        dispatchGesture(localBuilder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
    }



    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "ACC::onAccessibilityEvent: " + event.getEventType());

        //TYPE_WINDOW_STATE_CHANGED == 32
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event
                .getEventType()) {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            Log.i(TAG, "ACC::onAccessibilityEvent: nodeInfo=" + nodeInfo);
            if (nodeInfo == null) {
                return;
            }

            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByViewId("com.android.settings:id/left_button");
            for (AccessibilityNodeInfo node : list) {
                Log.i(TAG, "ACC::onAccessibilityEvent: left_button " + node);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            list = nodeInfo
                    .findAccessibilityNodeInfosByViewId("android:id/button1");
            for (AccessibilityNodeInfo node : list) {
                Log.i(TAG, "ACC::onAccessibilityEvent: button1 " + node);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    @Override
    public void onServiceConnected() {
        Log.i(TAG, "ACC::onServiceConnected: ");
    }


    @Override
    public void onInterrupt() {

    }
}
