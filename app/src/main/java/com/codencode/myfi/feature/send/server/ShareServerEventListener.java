package com.codencode.myfi.feature.send.server;

public interface ShareServerEventListener {
    void onDownloadProgress(int percentage, String speed);
}
