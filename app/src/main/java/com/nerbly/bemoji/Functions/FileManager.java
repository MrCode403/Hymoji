package com.nerbly.bemoji.Functions;

import android.os.Environment;

import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {


    final String path1 = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS + "/Bemojis");
    private final ArrayList<HashMap<String, Object>> filesList = new ArrayList<>();

    public ArrayList<HashMap<String, Object>> getList() {
        System.out.println(path1);
        java.io.File home = new java.io.File(path1);
        java.io.File[] listFiles = home.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (java.io.File file : listFiles) {
                System.out.println(file.getAbsolutePath());
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

        if (file.getName().startsWith("Bemoji") && (file.getName().endsWith(".jpg") || (file.getName().endsWith(".png") || file.getName().endsWith(".gif")))) {
            HashMap<String, Object> fileMap = new HashMap<>();

            fileMap.put("filePath", file.getPath());

            fileMap.put("modi_time", file.lastModified());

            filesList.add(fileMap);
        }
    }

}