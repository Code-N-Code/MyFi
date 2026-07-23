package com.codencode.myfi.feature.send.server;

import android.content.Context;

import com.codencode.myfi.core.http.HttpEndpoint;
import com.codencode.myfi.feature.send.domain.SharedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ShareServer extends NanoHTTPD {
    private final List<SharedFile> sharedFiles = new ArrayList<>();
    private final Map<String, HttpEndpoint> routes;
    private ShareServerEventListener eventListener;

    public ShareServer(int port, Context context) {
        super(port);
        routes = ShareRouteRegistry.create(context, sharedFiles, this::notifyDownloadProgress);
    }

    @Override
    public Response serve(IHTTPSession session) {
        HttpEndpoint endpoint = routes.get(session.getUri());
        if (endpoint != null) {
            return endpoint.handle(session);
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 - Page Not Found");
    }

    public void setSharedFiles(List<SharedFile> files) {
        sharedFiles.clear();
        sharedFiles.addAll(files);
    }

    public void setDownloadProgressListener(ShareServerEventListener listener) {
        eventListener = listener;
    }

    private void notifyDownloadProgress(int percentage, String speed) {
        if (eventListener != null) {
            eventListener.onDownloadProgress(percentage, speed);
        }
    }
}
