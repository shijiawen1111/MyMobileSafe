package com.example.mymobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

public class PreferenceUtils {
    private static SharedPreferences preferences = null;
    private static final String NAME = "mobilesafe";

    //拿到sharedPreference实例
    public static SharedPreferences getPreference(Context mContext) {
        if (preferences == null) {
            preferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static boolean getBoolean(Context mContext, String key) {
        return getBoolean(mContext, key, false);
    }

    public static boolean getBoolean(Context mContext, String key, boolean defValue) {
        SharedPreferences preference = getPreference(mContext);
        boolean preferenceBoolean = preference.getBoolean(key, defValue);
        return preferenceBoolean;
    }

    public static void setBoolean(Context mContext, String key, boolean value) {
        SharedPreferences preference = getPreference(mContext);
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static String getString(Context mContext, String key) {
        return getString(mContext, key, null);
    }

    public static String getString(Context mContext, String key, String defValue) {
        SharedPreferences preference = getPreference(mContext);
        return preference.getString(key, defValue);
    }

    public static void setString(Context mContext, String key, String value) {
        SharedPreferences preference = getPreference(mContext);
        SharedPreferences.Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static int getInt(Context mContext, String key) {
        return getInt(mContext, key, -1);
    }

    public static int getInt(Context mContext, String key, int value) {
        SharedPreferences preference = getPreference(mContext);
        return preference.getInt(key, value);
    }

    public static void setInt(Context mContext, String key, int value) {
        SharedPreferences preference = getPreference(mContext);
        SharedPreferences.Editor edit = preference.edit();
        edit.putInt(key, value);
        edit.commit();
    }
}
