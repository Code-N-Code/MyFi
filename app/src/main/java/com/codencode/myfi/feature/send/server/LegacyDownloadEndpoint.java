package com.codencode.myfi.feature.send.server;

import static fi.iki.elonen.NanoHTTPD.newChunkedResponse;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;
import android.net.Uri;

import com.codencode.myfi.core.http.HttpEndpoint;
import com.codencode.myfi.feature.send.domain.SharedFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class LegacyDownloadEndpoint implements HttpEndpoint {
    private final Context context;
    private final List<SharedFile> sharedFiles;

    public LegacyDownloadEndpoint(Context context, List<SharedFile> sharedFiles) {
        this.context = context;
        this.sharedFiles = sharedFiles;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        if (sharedFiles.isEmpty()) {
            return newFixedLengthResponse(
                    NanoHTTPD.Response.Status.NOT_FOUND,
                    "text/html",
                    "No file is currently being hosted."
            );
        }

        Uri sharedFileUri = sharedFiles.get(0).getUri();

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(sharedFileUri);
            return newChunkedResponse(
                    NanoHTTPD.Response.Status.OK,
                    "application/octet-stream",
                    inputStream
            );
        } catch (FileNotFoundException exception) {
            return newFixedLengthResponse(
                    NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    "text/plain",
                    "Error reading file: " + exception.getMessage()
            );
        }
    }
}
