package com.example.oksocketclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtils {

    private static SharedPreferences mSharedPreferences;

    public static void appInit(Context appContext) {
        mSharedPreferences = appContext.getSharedPreferences("jndv", MODE_PRIVATE);
    }

    public static String getString(String name) {
        return mSharedPreferences.getString(name, "");
    }

    public static String getString(String name, String defaultValue) {
        return mSharedPreferences.getString(name, defaultValue);
    }

    public static void saveString(String name, String value) {
        mSharedPreferences.edit().putString(name, value).commit();
    }

    public static int getInt(String name) {
        return mSharedPreferences.getInt(name, 0);
    }

    public static int getInt(String name, int defaultValue) {
        return mSharedPreferences.getInt(name, defaultValue);
    }

    public static void saveInt(String name, int value) {
        mSharedPreferences.edit().putInt(name, value).commit();
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        return mSharedPreferences.getBoolean(name, defaultValue);
    }

    public static boolean getBoolean(String name) {
        return mSharedPreferences.getBoolean(name, false);
    }

    public static void saveBoolean(String name, boolean value) {
        mSharedPreferences.edit().putBoolean(name, value).commit();
    }

    public static boolean contains(String name) {
        return mSharedPreferences.contains(name);
    }
}
