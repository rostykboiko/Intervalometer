package com.example.rosst.intervalometer.horizontalWheel;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

class SavedState extends BaseSavedState {
    double angle;

    SavedState(Parcel in) {
        super(in);
        this.angle = ((Double)in.readValue((ClassLoader)null)).doubleValue();
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };

    SavedState(Parcelable superState) {
        super(superState);
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeValue(Double.valueOf(this.angle));
    }

    public String toString() {
        return "HorizontalWheelView.SavedState{"
                + Integer.toHexString(System.identityHashCode(this))
                + " angle=" + this.angle + "}";
    }
}
