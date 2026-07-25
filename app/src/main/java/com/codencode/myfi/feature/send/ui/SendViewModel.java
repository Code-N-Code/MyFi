package com.codencode.myfi.feature.send.ui;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codencode.myfi.core.network.QrCodeGenerator;
import com.codencode.myfi.feature.send.ShareSessionManager;
import com.codencode.myfi.feature.send.domain.ShareSession;
import com.codencode.myfi.feature.send.domain.TransferProgress;

import java.io.IOException;

public class SendViewModel extends AndroidViewModel {
    private static final String READY_STATUS = "Server: Online (Ready to Share)";
    private static final String TRANSFER_IN_PROGRESS_STATUS = "Online (Transfer in Progress)";
    private static final String TRANSFER_COMPLETE_STATUS = "Online (Transfer Complete)";

    private final ShareSessionManager sessionManager;
    private final MutableLiveData<SendUiState> uiState = new MutableLiveData<>();

    private ShareSession shareSession;
    private Bitmap qrCode;
    private String statusMessage = READY_STATUS;
    private TransferProgress transferProgress;

    public SendViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new ShareSessionManager(application);
        sessionManager.setDownloadProgressListener(this::onDownloadProgress);
        shareSession = sessionManager.stopSharing();
        publishState();
    }

    @NonNull
    public LiveData<SendUiState> getUiState() {
        return uiState;
    }

    public void onScreenStarted() {
        try {
            shareSession = sessionManager.startSharing();
            qrCode = QrCodeGenerator.generate(shareSession.getServerUrl());
            publishState();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void onServerToggleClicked() {
        if (shareSession.isServerRunning()) {
            shareSession = sessionManager.stopSharing();
        } else {
            onScreenStarted();
            return;
        }

        publishState();
    }

    public void onFolderSelected(Uri folderUri) {
        shareSession = sessionManager.shareFolder(folderUri);
        publishState();
    }

    public void onScreenDestroyed() {
        shareSession = sessionManager.endSession();
        qrCode = null;
        transferProgress = null;
        statusMessage = READY_STATUS;
        publishState();
    }

    @Override
    protected void onCleared() {
        sessionManager.endSession();
        super.onCleared();
    }

    private void onDownloadProgress(int percentage, String speed) {
        if (percentage == 100) {
            transferProgress = null;
            statusMessage = TRANSFER_COMPLETE_STATUS;
        } else {
            transferProgress = new TransferProgress(percentage, speed);
            statusMessage = TRANSFER_IN_PROGRESS_STATUS;
        }

        publishState();
    }

    private void publishState() {
        uiState.setValue(new SendUiState(shareSession, statusMessage, qrCode, transferProgress));
    }
}
