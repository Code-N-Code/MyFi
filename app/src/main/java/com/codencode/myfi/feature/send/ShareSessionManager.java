package com.codencode.myfi.feature.send;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.codencode.myfi.core.network.LocalAddressProvider;
import com.codencode.myfi.feature.send.data.SafShareFolderRepository;
import com.codencode.myfi.feature.send.data.ShareFolderRepository;
import com.codencode.myfi.feature.send.domain.ShareSession;
import com.codencode.myfi.feature.send.domain.SharedFile;
import com.codencode.myfi.feature.send.server.ShareServer;
import com.codencode.myfi.feature.send.server.ShareServerEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ShareSessionManager {
    private static final int PORT = 8080;

    private final Context appContext;
    private final ShareFolderRepository folderRepository;
    private final ShareServer shareServer;
    private final List<SharedFile> sharedFiles = new ArrayList<>();

    private boolean serverRunning;
    private String serverUrl;

    public ShareSessionManager(@NonNull Context context) {
        appContext = context.getApplicationContext();
        folderRepository = new SafShareFolderRepository(appContext);
        shareServer = new ShareServer(PORT, appContext);
    }

    public ShareSession startSharing() throws IOException {
        if (!serverRunning) {
            shareServer.start();
            serverRunning = true;
            serverUrl = "http://" + LocalAddressProvider.getHotspotIpv4Address() + ":" + PORT;
        }

        return currentSession();
    }

    public ShareSession stopSharing() {
        if (serverRunning) {
            shareServer.stop();
            serverRunning = false;
        }

        return currentSession();
    }

    public ShareSession shareFolder(Uri folderUri) {
        int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        appContext.getContentResolver().takePersistableUriPermission(folderUri, takeFlags);

        List<SharedFile> selectedFiles = folderRepository.listSharedFiles(folderUri);
        if (!selectedFiles.isEmpty()) {
            sharedFiles.clear();
            sharedFiles.addAll(selectedFiles);
            shareServer.setSharedFiles(sharedFiles);
        }

        return currentSession();
    }

    public void setDownloadProgressListener(ShareServerEventListener listener) {
        shareServer.setDownloadProgressListener(listener);
    }

    public ShareSession endSession() {
        stopSharing();
        sharedFiles.clear();
        shareServer.setSharedFiles(sharedFiles);
        serverUrl = null;
        return currentSession();
    }

    private ShareSession currentSession() {
        return new ShareSession(serverRunning, serverUrl, sharedFiles.size());
    }
}
