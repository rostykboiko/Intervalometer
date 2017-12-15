package com.example.rosst.intervalometer.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.example.rosst.intervalometer.main.MainActivity;

import java.util.List;

public class Utils {
    public static int convertToPx(int dp, Resources resources) {
        DisplayMetrics dm = resources.getDisplayMetrics();
        return (int) TypedValue.applyDimension(1, (float)dp, dm);
    }

    public static List<ResolveInfo> getListOfApps(Context context){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appListInfo =
                context.getPackageManager().queryIntentActivities(mainIntent, 0);
        return appListInfo;
    }
}
