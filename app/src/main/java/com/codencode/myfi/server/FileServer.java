package com.codencode.myfi.server;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.codencode.myfi.filereader.model.FileEntry;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class FileServer extends NanoHTTPD {
    private final Context context;
    private Uri sharedFileUri;
    private List<FileEntry> fileEntryList;

    public FileServer(int port, Context context) {
        super(port);
        this.context = context;
        this.fileEntryList = new ArrayList<>();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();


        if(uri.equals("/")) {
            /*
            StringBuilder fileNameBuilder = new StringBuilder("<ul>");
            // We use a simple loop with an index so we can identify the file later
            for (int i = 0; i < fileEntryList.size(); i++) {
                FileEntry entry = fileEntryList.get(i);
                long sizeKB = entry.getSizeBytes() / 1024;

                // The magic happens here: we add a link with a query parameter "?id=0", "?id=1", etc.
                fileNameBuilder.append("<li>")
                        .append("<a href='/get-file?id=").append(i).append("'>")
                        .append(entry.getName())
                        .append("</a>")
                        .append(" (").append(sizeKB).append(" KB)</li>");
            }
            String htmlResponse = "<html>" +
                    "<head> <title> Android Server </title> </head>" +
                    "<body>" +
                    "<h1>Hello World!</h1>" +
                    fileNameBuilder +
                    "<p>This page is being served directly from an Android device.</p>" +
                    "</body>" +
                    "</html>";
            return newFixedLengthResponse(Response.Status.OK, "text/html", htmlResponse);

             */

            // 1. Prepare the Data Map
            Map<String, Object> scope = new HashMap<>();

            // Mustache needs a list of objects/maps it can read
            // We'll pass your fileEntryList directly
            // (Ensure your FileEntry has 'getName()' and 'getSizeBytes()' or public fields)
            scope.put("files", fileEntryList);


            try {
                // 2. Load the Template from Assets
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new InputStreamReader(context.getAssets().open("index.mustache")), "index");
                // 3. Execute (Combine Data + Template)
                StringWriter writer = new StringWriter();
                mustache.execute(writer, scope).flush();

                return newFixedLengthResponse(Response.Status.OK, "text/html", writer.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        if(uri.equals("/download")) {
            if(sharedFileUri == null) {
                if(fileEntryList.isEmpty()) {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "No file is currently being hosted.");
                } {
                    sharedFileUri = fileEntryList.get(0).getUri();
                }
            }

            try {
                // Using ContentResolver to safely get an InputStream from the SAF Uri
                InputStream inputStream = context.getContentResolver().openInputStream(sharedFileUri);

                // We use newChunkedResponse for streams where we might not know the exact file size,
                // or you can use newFixedLengthResponse if you query the Document provider for the size first.
                return newChunkedResponse(Response.Status.OK, "application/octet-stream", inputStream);

            } catch (FileNotFoundException e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error reading file: " + e.getMessage());
            }
        }

        if(uri.equals("/get-file")) {
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
                    Response response = newFixedLengthResponse(Response.Status.OK, mimeType, bufferedStream, fileSize);

                    // 2. Add the Content-Disposition header
                    // 'attachment' tells the browser to download it instead of trying to play it in-browser
                    // 'filename="..."' tells the browser what to call the saved file
                    response.addHeader("Content-Disposition", "attachment; filename=\"" + selectedEntry.getName() + "\"");
                    return response;
                } catch (FileNotFoundException e) {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "File Error: " + e.getMessage());
                }
            }
        }

        // Catch-all for 404 Not Found
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 - Page Not Found");
    }

    public void setFileMap(List<FileEntry> fileList) {
        this.fileEntryList = fileList;
    }

}
