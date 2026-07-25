package com.codencode.myfi.feature.send.server;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.codencode.myfi.core.format.FileSizeFormatter;
import com.codencode.myfi.core.format.MimeTypeResolver;
import com.codencode.myfi.core.io.ProgressInputStream;
import com.codencode.myfi.feature.send.domain.SharedFile;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public final class FileDownloadResponseFactory {
    private static final int BUFFER_SIZE = 128 * 1024;

    private FileDownloadResponseFactory() {
    }

    public static NanoHTTPD.Response create(
            Context context,
            SharedFile file,
            ShareServerEventListener progressListener
    ) {
        try {
            InputStream rawStream = context.getContentResolver().openInputStream(file.getUri());
            InputStream bufferedStream = new BufferedInputStream(rawStream, BUFFER_SIZE);

            ProgressInputStream progressInputStream = new ProgressInputStream(
                    bufferedStream,
                    file.getSizeBytes(),
                    (percentage, bytesRead, totalSize, speedBytesPerSec) ->
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (progressListener != null) {
                                    String speedText = FileSizeFormatter.format((long) speedBytesPerSec) + "/s";
                                    progressListener.onDownloadProgress(percentage, speedText);
                                }
                            })
            );

            String mimeType = MimeTypeResolver.fromFileName(file.getName());
            NanoHTTPD.Response response = newFixedLengthResponse(
                    NanoHTTPD.Response.Status.OK,
                    mimeType,
                    progressInputStream,
                    file.getSizeBytes()
            );
            response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            return response;
        } catch (FileNotFoundException exception) {
            return newFixedLengthResponse(
                    NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    "text/plain",
                    "File Error: " + exception.getMessage()
            );
        }
    }
}
