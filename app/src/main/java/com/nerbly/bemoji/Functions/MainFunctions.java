package com.nerbly.bemoji.Functions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

public class MainFunctions {

    //languages
    public static void setLocale(String lang, int position, Activity context) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getBaseContext().getResources().updateConfiguration(config, context.getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor shared1 = context.getSharedPreferences("AppData", Activity.MODE_PRIVATE).edit();
        shared1.putString("language", lang);
        shared1.putInt("lang_pos", position);
        shared1.apply();
    }

    public static void setFragmentLocale(String lang, int position, View view) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        view.getContext().getResources().updateConfiguration(config, view.getContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor shared1 = view.getContext().getSharedPreferences("AppData", Activity.MODE_PRIVATE).edit();
        shared1.putString("language", lang);
        shared1.putInt("lang_pos", position);
        shared1.apply();
    }

    public static void loadFragmentLocale(View view) {
        SharedPreferences shared = view.getContext().getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        if (shared.getString("language", "") != null) {
            if (!shared.getString("language", "").equals("")) {
                setFragmentLocale(shared.getString("language", ""), shared.getInt("lang_pos", -1), view);
            }
        }
    }

    public static void loadLocale(Activity context) {
        SharedPreferences shared = context.getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        if (shared.getString("language", "") != null) {
            if (!shared.getString("language", "").isEmpty()) {
                setLocale(shared.getString("language", ""), shared.getInt("lang_pos", -1), context);
            }
        }

    }

    //naming
    public static String capitalizedFirstWord(String data) {
        StringBuilder out = new StringBuilder();
        String[] arr = data.trim().split(" ");
        for (String s : arr) {
            if (s.isEmpty()) continue;
            out.append(String.valueOf(s.charAt(0)).toUpperCase());
            if (s.length() > 1) {
                out.append(s.substring(1));
            }
            out.append(" ");
        }
        return out.toString().trim();
    }


    //screen utils
    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }


    //cache related methods
    public static void trimCache(Activity context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        assert dir != null;
        return dir.delete();
    }

    public static long getDirSize(File dir) {
        long size = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String initializeCacheScan(Activity context) {
        long size = 0;
        try {
            size += getDirSize(context.getCacheDir());
            size += getDirSize(context.getExternalCacheDir());
        } catch (Exception ignored) {
        }
        return readableFileSize(size);
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}