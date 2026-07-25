package com.codencode.myfi.feature.send.server;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;

import com.codencode.myfi.core.http.HttpEndpoint;
import com.codencode.myfi.feature.send.domain.SharedFile;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class FileDownloadEndpoint implements HttpEndpoint {
    private final Context context;
    private final List<SharedFile> sharedFiles;
    private final ShareServerEventListener progressListener;

    public FileDownloadEndpoint(
            Context context,
            List<SharedFile> sharedFiles,
            ShareServerEventListener progressListener
    ) {
        this.context = context;
        this.sharedFiles = sharedFiles;
        this.progressListener = progressListener;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> idParameters = parameters.get("id");

        if (idParameters != null && !idParameters.isEmpty()) {
            try {
                int index = Integer.parseInt(idParameters.get(0));
                if (index >= 0 && index < sharedFiles.size()) {
                    SharedFile selectedFile = sharedFiles.get(index);
                    return FileDownloadResponseFactory.create(context, selectedFile, progressListener);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return newFixedLengthResponse(
                NanoHTTPD.Response.Status.NOT_FOUND,
                "text/plain",
                "404 - File Not Found"
        );
    }
}
