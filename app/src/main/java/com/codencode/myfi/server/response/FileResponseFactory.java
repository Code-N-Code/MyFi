package com.codencode.myfi.server.response;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.codencode.myfi.filereader.model.FileEntry;
import com.codencode.myfi.ui.ProgressCallback;
import com.codencode.myfi.utils.FileUtility;
import com.codencode.myfi.utils.ProgressInputStream;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class FileResponseFactory {
    private static final int BUFFER_SIZE = 128 * 1024; // 128 KB

    public static NanoHTTPD.Response createResponse(Context context, FileEntry entry, ProgressCallback uiCallback) {
        try {
            InputStream rawStream = context.getContentResolver().openInputStream(entry.getUri());
            InputStream bufferedStream = new BufferedInputStream(rawStream, BUFFER_SIZE);

            ProgressInputStream progressInputStream = new ProgressInputStream(bufferedStream, entry.getSizeBytes(),
                    (percentage, bytesRead, totalSize, speedBytesPerSec) -> new Handler(Looper.getMainLooper()).post(() -> {
                        if (uiCallback != null) {
                            String speedText = FileUtility.formatSize((long) speedBytesPerSec) + "/s";
                            uiCallback.updateProgressBar(percentage, speedText);
                        }
                    }));

            String mimeType = FileUtility.getMimeType(entry.getName());
            NanoHTTPD.Response response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeType, progressInputStream, entry.getSizeBytes());
            response.addHeader("Content-Disposition", "attachment; filename=\"" + entry.getName() + "\"");
            return response;
        } catch (FileNotFoundException e) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "File Error: " + e.getMessage());
        }
    }
}
