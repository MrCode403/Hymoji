package com.nerbly.bemoji.Functions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdSize;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void ZIP(String source, String destination) {
        zipFileAtPath(source, destination);
    }

    public static void zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, Objects.requireNonNull(sourceFile.getParent()).length());
            } else {
                byte[] data = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin;
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte[] data = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath.substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public static String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "PackEmojis";
        return segments[segments.length - 1];

    }

    public static void sortListMap(final ArrayList<HashMap<String, Object>> listMap, final String key, final boolean isNumber, final boolean ascending) {
        Collections.sort(listMap, (compareMap1, compareMap2) -> {
            try {
                if (isNumber) {
                    int count1 = Integer.parseInt(Objects.requireNonNull(compareMap1.get(key)).toString());
                    int count2 = Integer.parseInt(Objects.requireNonNull(compareMap2.get(key)).toString());
                    if (ascending) {
                        return count1 < count2 ? -1 : 0;
                    } else {
                        return count1 > count2 ? -1 : 0;
                    }
                } else {
                    if (ascending) {
                        return (Objects.requireNonNull(compareMap1.get(key)).toString()).compareTo(Objects.requireNonNull(compareMap2.get(key)).toString());
                    } else {
                        return (Objects.requireNonNull(compareMap2.get(key)).toString()).compareTo(Objects.requireNonNull(compareMap1.get(key)).toString());
                    }
                }
            } catch (Exception e) {
                return 0;
            }
        });
    }

    public static ArrayList<HashMap<String, Object>> getLocalEmojisMediaStore(@NonNull Context context) {
        ArrayList<HashMap<String, Object>> imagesList = new ArrayList<>();
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME
        };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            String fileName = cursor.getString(1).toLowerCase();

            if (fileName.startsWith("hymoji") && (fileName.endsWith(".jpg") || (fileName.endsWith(".png") || fileName.endsWith(".gif")))) {
                HashMap<String, Object> imagesMap = new HashMap<>();
                imagesMap.put("filePath", absolutePathOfImage);
                imagesList.add(imagesMap);
            }
        }
        cursor.close();
        Collections.reverse(imagesList);
        return imagesList;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isStoragePermissionGranted(Context context) {
        boolean read_storage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean write_storage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        Log.d("PERMISSIONS", "Read storage granted: " + read_storage);
        Log.d("PERMISSIONS", "Write storage granted: " + write_storage);

        if (Build.VERSION.SDK_INT >= 30) {
            return read_storage;
        } else {
            return read_storage && write_storage;
        }
    }

    public static void requestStoragePermission(int requestCode, Activity context) {
        if (Build.VERSION.SDK_INT >= 30) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }

    public static AdSize getAdSize(LinearLayout view, Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;
        float adWidthPixels = view.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
