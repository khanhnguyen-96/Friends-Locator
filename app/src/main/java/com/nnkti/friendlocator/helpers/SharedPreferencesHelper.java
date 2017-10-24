package com.nnkti.friendlocator.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

/**
 * Created by nnkti on 10/22/2017.
 */

public class SharedPreferencesHelper {
    public static void writeStringSharedPreferences(FragmentActivity activityContext, String key, String value) {
        activityContext.getPreferences(Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }
    public static String readStringSharedPreferences(FragmentActivity activityContext, String key) {
        return activityContext.getPreferences(Context.MODE_PRIVATE).getString(key, "");
    }

    public static void writeDoubleSharedPreferences(FragmentActivity activityContext, final String key, final double value) {
        activityContext.getPreferences(Context.MODE_PRIVATE).edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    public static double readDoubleSharedPreferences(FragmentActivity activityContext, final String key, final double defaultValue) {
        return Double.longBitsToDouble(activityContext.getPreferences(Context.MODE_PRIVATE).getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
