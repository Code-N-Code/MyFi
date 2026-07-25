package com.codencode.myfi.feature.send.domain;

import androidx.annotation.Nullable;

public final class ShareSession {
    private final boolean serverRunning;
    @Nullable
    private final String serverUrl;
    private final int sharedFileCount;

    public ShareSession(boolean serverRunning, @Nullable String serverUrl, int sharedFileCount) {
        this.serverRunning = serverRunning;
        this.serverUrl = serverUrl;
        this.sharedFileCount = sharedFileCount;
    }

    public boolean isServerRunning() {
        return serverRunning;
    }

    @Nullable
    public String getServerUrl() {
        return serverUrl;
    }

    public int getSharedFileCount() {
        return sharedFileCount;
    }
}
