package com.codencode.myfi.core.storage;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DocumentTextReader {
    private final ContentResolver contentResolver;

    public DocumentTextReader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public String readText(Uri uri) throws IOException {
        StringBuilder text = new StringBuilder();

        try (InputStream inputStream = contentResolver.openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        }

        return text.toString();
    }
}
