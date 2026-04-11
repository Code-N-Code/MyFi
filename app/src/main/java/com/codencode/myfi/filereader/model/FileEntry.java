package com.codencode.myfi.filereader.model;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codencode.myfi.utils.FileUtility;

public final class FileEntry {

    private final int id;
    private final String name;       // the visible filename, e.g. "report.pdf"
    private final Uri uri;           // the SAF address of this file — used to open/read it later
    private final String mimeType;   // file type hint e.g. "application/pdf", can be null
    private final long sizeBytes;    // file size in bytes
    private final boolean isDirectory;

    public FileEntry(
            @NonNull String name,
            @NonNull Uri uri,
            @Nullable String mimeType,
            long sizeBytes,
            boolean isDirectory,
            int id
    ) {
        this.name         = name;
        this.uri          = uri;
        this.mimeType     = mimeType;
        this.sizeBytes    = sizeBytes;
        this.isDirectory = isDirectory;
        this.id = id;
    }

    @NonNull public String getName()        { return name; }
    @NonNull public Uri    getUri()         { return uri; }
    @Nullable public String getMimeType()   { return mimeType; }
    public long getSizeBytes()              { return sizeBytes; }
    public boolean getIsDirectory()         { return isDirectory; }
    public int getId()                      { return id; }

    public String getSize() {
        return FileUtility.formatSize(sizeBytes);
    }

    @NonNull
    @Override
    public String toString() {
        return "FileEntry{name='" + name + "', uri=" + uri + '}';
    }
}