package com.codencode.myfi.feature.send.ui;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codencode.myfi.feature.send.domain.ShareSession;
import com.codencode.myfi.feature.send.domain.TransferProgress;

public final class SendUiState {
    private final ShareSession shareSession;
    private final String statusMessage;
    @Nullable
    private final Bitmap qrCode;
    @Nullable
    private final TransferProgress transferProgress;

    public SendUiState(
            @NonNull ShareSession shareSession,
            @NonNull String statusMessage,
            @Nullable Bitmap qrCode,
            @Nullable TransferProgress transferProgress
    ) {
        this.shareSession = shareSession;
        this.statusMessage = statusMessage;
        this.qrCode = qrCode;
        this.transferProgress = transferProgress;
    }

    @NonNull
    public ShareSession getShareSession() {
        return shareSession;
    }

    @NonNull
    public String getStatusMessage() {
        return statusMessage;
    }

    @Nullable
    public Bitmap getQrCode() {
        return qrCode;
    }

    @Nullable
    public TransferProgress getTransferProgress() {
        return transferProgress;
    }
}
