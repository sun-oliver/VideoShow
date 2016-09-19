package com.helloworld.videoshow.utils;

import android.graphics.Bitmap;

/**
 * Created by Du on 2015/9/18.
 */
public class CommonUtils {

    public static String safeString(String val, String defaultVal) {
        if (val == null || val.equals("")) {
            return defaultVal;
        }
        return val;
    }

    public static int getSizeInBytes(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static String getExtFromString(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (fileName.lastIndexOf('.') > 0) {
            extension = fileName.substring(i + 1);
            return getValidateVideoExt(extension);
        } else {
            return "";
        }
    }

    public static String getSafeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        return url.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace("'", "");
    }

    public static String getFileName(String fileName) {
        if (fileName.isEmpty()) {
            return "";
        }
        return fileName.replace(" ", "_")
                .replace("/", "")
                .replace("@", "")
                .replace("#", "")
                .replace("$", "")
                .replace("%", "")
                .replace("^", "")
                .replace("&", "")
                .replace("*", "")
                .replace("(", "")
                .replace(")", "")
                .replace("[", "")
                .replace("]", "")
                .replace("|", "")
                .replace("-", "");
    }

    public static String getValidateVideoExt(String ext) {
        String[] allowFormats = {
                "mp4", "3gp", "webm", "mkv", "asf", "avi", "mov", "flv", "rm", "rmvb"
        };
        for (String s : allowFormats) {
            if (s.equals(ext.toLowerCase())) {
                return ext;
            }
        }
        return "mp4";
    }
}
