package com.nerbly.bemoji.Functions;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {


    public static void sortListMap(final ArrayList<HashMap<String, Object>> listMap, final String key, final boolean isNumber, final boolean ascending) {
        Collections.sort(listMap, new Comparator<HashMap<String, Object>>() {
            public int compare(HashMap<String, Object> _compareMap1, HashMap<String, Object> _compareMap2) {
                if (isNumber) {
                    int _count1 = Integer.parseInt(Objects.requireNonNull(_compareMap1.get(key)).toString());
                    int _count2 = Integer.parseInt(Objects.requireNonNull(_compareMap2.get(key)).toString());
                    if (ascending) {
                        return _count1 < _count2 ? -1 : _count1 < _count2 ? 1 : 0;
                    } else {
                        return _count1 > _count2 ? -1 : _count1 > _count2 ? 1 : 0;
                    }
                } else {
                    if (ascending) {
                        return (Objects.requireNonNull(_compareMap1.get(key)).toString()).compareTo(Objects.requireNonNull(_compareMap2.get(key)).toString());
                    } else {
                        return (Objects.requireNonNull(_compareMap2.get(key)).toString()).compareTo(Objects.requireNonNull(_compareMap1.get(key)).toString());
                    }
                }
            }
        });
    }

    public static void showMessage(Context _context, String _s) {
        Toast.makeText(_context, _s, Toast.LENGTH_SHORT).show();
    }


    public static void ZIP(final String _source, final String _destination) {
        zipFileAtPath(_source, _destination);
    }

    public static void zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
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

    private static void zipSubFolder(ZipOutputStream out, File folder,
                                     int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte[] data = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
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

    public static void sortListMap2(final ArrayList<HashMap<String, Object>> listMap, final String key, final boolean isNumber, final boolean ascending) {
        Collections.sort(listMap, new Comparator<HashMap<String, Object>>() {
            public int compare(HashMap<String, Object> _compareMap1, HashMap<String, Object> _compareMap2) {
                if (isNumber) {
                    int _count1 = Integer.parseInt(Objects.requireNonNull(_compareMap1.get(key)).toString());
                    int _count2 = Integer.parseInt(Objects.requireNonNull(_compareMap2.get(key)).toString());
                    if (ascending) {
                        return _count1 < _count2 ? -1 : _count1 < _count2 ? 1 : 0;
                    } else {
                        return _count1 > _count2 ? -1 : _count1 > _count2 ? 1 : 0;
                    }
                } else {
                    if (ascending) {
                        return (Objects.requireNonNull(_compareMap1.get(key)).toString()).compareTo(Objects.requireNonNull(_compareMap2.get(key)).toString());
                    } else {
                        return (Objects.requireNonNull(_compareMap2.get(key)).toString()).compareTo(Objects.requireNonNull(_compareMap1.get(key)).toString());
                    }
                }
            }
        });
    }

}
