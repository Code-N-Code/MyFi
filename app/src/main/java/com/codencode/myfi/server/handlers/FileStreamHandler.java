package com.codencode.myfi.server.handlers;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;

import com.codencode.myfi.feature.send.domain.SharedFile;
import com.codencode.myfi.server.RouteHandler;
import com.codencode.myfi.server.response.FileResponseFactory;
import com.codencode.myfi.ui.ProgressCallback;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class FileStreamHandler implements RouteHandler {
    private final Context context;
    private final List<SharedFile> fileEntryList;
    private final ProgressCallback uiCallback;

    public FileStreamHandler(Context context, List<SharedFile> fileEntryList, ProgressCallback uiCallback) {
        this.context = context;
        this.fileEntryList = fileEntryList;
        this.uiCallback = uiCallback;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> params = session.getParameters();
        List<String> idParams = params.get("id");

        if (idParams != null && !idParams.isEmpty()) {
            try {
                int index = Integer.parseInt(idParams.get(0));
                if (index >= 0 && index < fileEntryList.size()) {
                    SharedFile selectedEntry = fileEntryList.get(index);
                    return FileResponseFactory.createResponse(context, selectedEntry, uiCallback);
                }
            } catch (NumberFormatException ignored) {}
        }
        return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 - File Not Found");
    }
}
