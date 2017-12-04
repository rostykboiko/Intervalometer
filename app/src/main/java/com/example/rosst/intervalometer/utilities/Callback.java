package com.example.rosst.intervalometer.utilities;

public interface Callback {
    void callBackDuration(int duration);
    void callBackFrames(int currentNum, int numOfFrames);
}
