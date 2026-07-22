package com.codencode.myfi.feature.send.domain;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codencode.myfi.core.format.FileSizeFormatter;

public final class SharedFile {
    private final int id;
    private final String name;
    private final Uri uri;
    private final String mimeType;
    private final long sizeBytes;
    private final boolean directory;

    public SharedFile(
            @NonNull String name,
            @NonNull Uri uri,
            @Nullable String mimeType,
            long sizeBytes,
            boolean directory,
            int id
    ) {
        this.name = name;
        this.uri = uri;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.directory = directory;
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public Uri getUri() {
        return uri;
    }

    @Nullable
    public String getMimeType() {
        return mimeType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public boolean isDirectory() {
        return directory;
    }

    public int getId() {
        return id;
    }

    public String getSize() {
        return FileSizeFormatter.format(sizeBytes);
    }

    @NonNull
    @Override
    public String toString() {
        return "SharedFile{name='" + name + "', uri=" + uri + '}';
    }
}
