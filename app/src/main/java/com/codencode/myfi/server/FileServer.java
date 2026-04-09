package com.codencode.myfi.server;

import android.content.Context;

import com.codencode.myfi.filereader.model.FileEntry;
import com.codencode.myfi.server.handlers.DownloadHandler;
import com.codencode.myfi.server.handlers.FileStreamHandler;
import com.codencode.myfi.server.handlers.IndexHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class FileServer extends NanoHTTPD {
    private final Context context;
    private final List<FileEntry> fileEntryList;
    private final Map<String, RouteHandler> routes = new HashMap<>();
    private ServerEventListener eventListener;

    public FileServer(int port, Context context) {
        super(port);
        this.context = context;
        this.fileEntryList = new ArrayList<>();
        initializeRoutes();
    }

    private void initializeRoutes() {
        routes.put("/", new IndexHandler(context, fileEntryList));
        routes.put("/download", new DownloadHandler(context, fileEntryList));
        routes.put("/get-file", new FileStreamHandler(context, fileEntryList,
                percentage -> {
                    if (eventListener != null) {
                        eventListener.onDownloadProgress(percentage);
                    }
                }));
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        RouteHandler handler = routes.get(uri);

        if (handler != null) {
            return handler.handle(session);
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 - Page Not Found");
    }

    public void setFileMap(List<FileEntry> fileList) {
        this.fileEntryList.clear();
        this.fileEntryList.addAll(fileList);
    }

    public void setEventListener(ServerEventListener eventListener) {
        this.eventListener = eventListener;
    }

}
