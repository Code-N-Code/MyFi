package com.codencode.myfi.server.handlers;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;

import com.codencode.myfi.filereader.model.FileEntry;
import com.codencode.myfi.server.RouteHandler;
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

public class IndexHandler implements RouteHandler {
    private final Context context;
    private final List<FileEntry> fileEntryList;

    public IndexHandler(Context context, List<FileEntry> fileEntryList) {
        this.context = context;
        this.fileEntryList = fileEntryList;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {
        // 1. Prepare the Data Map
        Map<String, Object> scope = new HashMap<>();

        // Mustache needs a list of objects/maps it can read
        // We'll pass fileEntryList directly
        scope.put("files", fileEntryList);


        try {
            // 2. Load the Template from Assets
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new InputStreamReader(context.getAssets().open("index.mustache")), "index");
            // 3. Execute (Combine Data + Template)
            StringWriter writer = new StringWriter();
            mustache.execute(writer, scope).flush();

            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", writer.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
