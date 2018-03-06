package com.example.rosst.intervalometer.main.aboutDialog;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private Drawable appIcon;
    private CharSequence appLabel;
    private String appPackage;


    public AppInfo(Drawable appIcon, CharSequence appLabel, String appPackage) {
        this.appIcon = appIcon;
        this.appLabel = appLabel;
        this.appPackage = appPackage;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public CharSequence getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(CharSequence appLabel) {
        this.appLabel = appLabel;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }
}
