package com.codencode.myfi.feature.send.server;

import android.content.Context;

import com.codencode.myfi.core.http.HttpEndpoint;
import com.codencode.myfi.feature.send.domain.SharedFile;
import com.codencode.myfi.ui.ProgressCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShareRouteRegistry {
    private ShareRouteRegistry() {
    }

    public static Map<String, HttpEndpoint> create(
            Context context,
            List<SharedFile> sharedFiles,
            ProgressCallback progressCallback
    ) {
        Map<String, HttpEndpoint> routes = new HashMap<>();
        routes.put("/", new IndexPageEndpoint(context, sharedFiles));
        routes.put("/download", new LegacyDownloadEndpoint(context, sharedFiles));
        routes.put("/get-file", new FileDownloadEndpoint(context, sharedFiles, progressCallback));
        return routes;
    }
}
