package com.codencode.myfi.server.handlers;

import static fi.iki.elonen.NanoHTTPD.newChunkedResponse;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;
import android.net.Uri;

import com.codencode.myfi.filereader.model.FileEntry;
import com.codencode.myfi.server.RouteHandler;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class DownloadHandler implements RouteHandler {
    private final Context context;
    private final List<FileEntry> fileEntryList;

    public DownloadHandler(Context context, List<FileEntry> fileEntryList) {
        this.context = context;
        this.fileEntryList = fileEntryList;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        if(fileEntryList.isEmpty()) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/html", "No file is currently being hosted.");
        }
        Uri sharedFileUri = fileEntryList.get(0).getUri();

        try {
            // Using ContentResolver to safely get an InputStream from the SAF Uri
            InputStream inputStream = context.getContentResolver().openInputStream(sharedFileUri);

            // We use newChunkedResponse for streams where we might not know the exact file size,
            // or you can use newFixedLengthResponse if you query the Document provider for the size first.
            return newChunkedResponse(NanoHTTPD.Response.Status.OK, "application/octet-stream", inputStream);

        } catch (FileNotFoundException e) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "Error reading file: " + e.getMessage());
        }
    }
}
