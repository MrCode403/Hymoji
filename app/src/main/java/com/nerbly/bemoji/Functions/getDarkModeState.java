package com.nerbly.bemoji.Functions;

import android.content.Context;
import android.content.SharedPreferences;

public class getDarkModeState {
    private static SharedPreferences mySharedPref;

    public getDarkModeState(Context context) {
        mySharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
    }

    public static void setNightModeState(int state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putInt("currentTheme", state);
        editor.apply();
    }

    public int loadNightModeState() {
        return mySharedPref.getInt("currentTheme", 0);
    }
}