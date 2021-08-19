package com.nerbly.bemoji.Functions;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {

    String pathToScan = Environment.DIRECTORY_DOWNLOADS + "/Hymoji/";

    private final ArrayList<HashMap<String, Object>> filesList = new ArrayList<>();


    public ArrayList<HashMap<String, Object>> getList() {
        java.io.File home = new java.io.File(pathToScan);
        java.io.File[] listFiles = home.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (java.io.File file : listFiles) {
                if (file.isDirectory()) {
                    if (!file.isHidden()) {
                        scanDirectory(file);
                    }
                } else {
                    if (!file.isHidden()) {
                        addFileToList(file);
                    }
                }
            }
        }
        // return file list array
        return filesList;
    }

    private void scanDirectory(java.io.File directory) {
        if (directory != null) {
            if (!directory.isHidden()) {
                if (!directory.getName().equals("Android")) {
                    java.io.File[] listFiles = directory.listFiles();
                    if (listFiles != null && listFiles.length > 0) {
                        for (java.io.File file : listFiles) {
                            if (file.isDirectory()) {
                                if (!file.isHidden()) {
                                    scanDirectory(file);
                                }

                            } else {
                                if (!file.isHidden()) {
                                    addFileToList(file);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addFileToList(java.io.File file) {
        if (file.getName().startsWith("hymoji") && (file.getName().endsWith(".jpg") || (file.getName().endsWith(".png") || file.getName().endsWith(".gif")))) {
            HashMap<String, Object> fileMap = new HashMap<>();
            fileMap.put("filePath", file.getPath());
            fileMap.put("lastModifiedTime", file.lastModified());
            filesList.add(fileMap);
        }
    }


    private final ArrayList<HashMap<String, Object>> localEmojisList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<HashMap<String, Object>> getLocalEmojisFrom(Context context) {

        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.MediaColumns.TITLE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.RELATIVE_PATH
        };

        String selection = MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? ";


        String[] selections = new String[]{"%" + pathToScan + "%"};

        Cursor cursor = context.getContentResolver().query(externalUri, projection, selection, selections, MediaStore.Images.Media.DATE_TAKEN);


        int idColumn = cursor.getColumnIndex(MediaStore.MediaColumns._ID);

        while (cursor.moveToNext()) {
            Uri photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(idColumn));
            String fileName = cursor.getString(4).toLowerCase();
            if (fileName.startsWith("hymoji") && (fileName.endsWith(".jpg") || (fileName.endsWith(".png") || fileName.endsWith(".gif")))) {
                HashMap<String, Object> localEmojisMap = new HashMap<>();
                localEmojisMap.put("filePath", photoUri.toString());
                localEmojisList.add(localEmojisMap);
            }
        }
        cursor.close();

        return localEmojisList;
    }


}