package com.codencode.myfi.core.format;

import android.webkit.MimeTypeMap;

public final class MimeTypeResolver {
    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    private MimeTypeResolver() {
    }

    public static String fromFileName(String fileName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        if (extension == null) {
            return DEFAULT_MIME_TYPE;
        }

        String mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension.toLowerCase());
        return mimeType != null ? mimeType : DEFAULT_MIME_TYPE;
    }
}
