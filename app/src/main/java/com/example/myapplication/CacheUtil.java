package com.example.myapplication;

import android.content.Context;

import java.io.File;

public class CacheUtil {

    public static void deleteCache(Context context) {
        try {
            File cacheDirectory = context.getCacheDir();
            deleteDir(cacheDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            String[] children = file.list();
            for (String child : children) {
                boolean success = deleteDir(new File(file, child));
                if (!success) {
                    return false;
                }
            }
        }
        return file.delete();
    }
}
