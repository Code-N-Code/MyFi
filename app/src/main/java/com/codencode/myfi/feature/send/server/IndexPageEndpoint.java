package com.codencode.myfi.feature.send.server;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;

import com.codencode.myfi.core.http.HttpEndpoint;
import com.codencode.myfi.feature.send.domain.SharedFile;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class IndexPageEndpoint implements HttpEndpoint {
    private final Context context;
    private final List<SharedFile> sharedFiles;

    public IndexPageEndpoint(Context context, List<SharedFile> sharedFiles) {
        this.context = context;
        this.sharedFiles = sharedFiles;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        Map<String, Object> scope = new HashMap<>();
        scope.put("files", sharedFiles);

        try {
            MustacheFactory factory = new DefaultMustacheFactory();
            Mustache mustache = factory.compile(
                    new InputStreamReader(context.getAssets().open("index.mustache")),
                    "index"
            );
            StringWriter writer = new StringWriter();
            mustache.execute(writer, scope).flush();

            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", writer.toString());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
