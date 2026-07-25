package com.codencode.myfi.feature.send.domain;

import androidx.annotation.NonNull;

public final class TransferProgress {
    private final int percentage;
    private final String speed;

    public TransferProgress(int percentage, @NonNull String speed) {
        this.percentage = percentage;
        this.speed = speed;
    }

    public int getPercentage() {
        return percentage;
    }

    @NonNull
    public String getSpeed() {
        return speed;
    }
}
