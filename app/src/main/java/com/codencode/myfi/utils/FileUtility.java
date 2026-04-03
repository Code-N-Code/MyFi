package com.codencode.myfi.utils;

import java.util.Locale;

public class FileUtility {
    private static final String SIZE_FORMAT = "KMGTPE";

    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = SIZE_FORMAT.charAt(exp - 1) + "";
        // Explicitly using Locale.US ensures a '.' is always used for decimals
        return String.format(Locale.US, "%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
