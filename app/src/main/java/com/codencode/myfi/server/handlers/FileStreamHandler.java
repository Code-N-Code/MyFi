package com.codencode.myfi.server.handlers;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.codencode.myfi.filereader.model.FileEntry;
import com.codencode.myfi.server.RouteHandler;
import com.codencode.myfi.ui.ProgressCallback;
import com.codencode.myfi.utils.ProgressInputStream;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class FileStreamHandler implements RouteHandler {
    private final Context context;
    private final List<FileEntry> fileEntryList;
    private final ProgressCallback uiCallback;

    public FileStreamHandler(Context context, List<FileEntry> fileEntryList, ProgressCallback uiCallback) {
        this.context = context;
        this.fileEntryList = fileEntryList;
        this.uiCallback = uiCallback;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> params = session.getParameters();
        List<String> idParams = params.get("id");

        if(idParams != null && !idParams.isEmpty()) {
            int index = Integer.parseInt(idParams.get(0));
            FileEntry selectedEntry = fileEntryList.get(index);

            // 1. Open the stream via ContentResolver
            try {
                InputStream rawStream = context.getContentResolver().openInputStream(selectedEntry.getUri());

                // Wrap the stream in a BufferedInputStream with a 128KB buffer
                // This reduces the number of 'read' calls to the Android storage system
                int bufferSize = 128 * 1024; // 128 KB
                InputStream bufferedStream = new BufferedInputStream(rawStream, bufferSize);

                // 2. Get the actual size for better performance (Fixed Length)
                long fileSize = selectedEntry.getSizeBytes();

                ProgressInputStream progressInputStream = new ProgressInputStream(bufferedStream, selectedEntry.getSizeBytes(),
                        new ProgressInputStream.ProgressListener() {
                            @Override
                            public void onProgressUpdate(int percentage, long bytesRead, long totalSize) {
                                new Handler(Looper.getMainLooper()).post(
                                        () -> {
                                            if(uiCallback != null) {
                                                uiCallback.updateProgressBar(percentage);
                                            }
                                        }
                                );
                            }
                        });

                // 3. Determine the MIME type (e.g., video/mp4)
                String mimeType = "application/octet-stream"; // Default
                String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(selectedEntry.getName());
                if (extension != null) {
                    String newMimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
                    if(newMimeType != null) {
                        mimeType = newMimeType;
                    }
                }
                // Return the file stream
                Log.i("CodeNCode", mimeType);
                NanoHTTPD.Response response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeType, progressInputStream, fileSize);

                // 2. Add the Content-Disposition header
                response.addHeader("Content-Disposition", "attachment; filename=\"" + selectedEntry.getName() + "\"");
                return response;
            } catch (FileNotFoundException e) {
                return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "File Error: " + e.getMessage());
            }
        }
        return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 - Page Not Found");
    }
}
