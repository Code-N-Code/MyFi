package com.codencode.myfi.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends FilterInputStream {
    private long totalBytesRead = 0;
    private final long totalSize;
    private int lastNotifiedPercent = -1;
    private final ProgressListener listener;

    public interface ProgressListener {
        void onProgressUpdate(int percentage, long bytesRead, long totalSize);
    }

    public ProgressInputStream(InputStream in, long totalSize, ProgressListener listener) {
        super(in);
        this.totalSize = totalSize;
        this.listener = listener;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        // Let the actual stream read the data
        int bytesReadThisTime = super.read(b, off, len);

        if (bytesReadThisTime != -1) {
            totalBytesRead += bytesReadThisTime;

            // Calculate progress safely to avoid division by zero
            if (totalSize > 0) {
                int currentPercent = (int) ((totalBytesRead * 100) / totalSize);

                // Only notify the UI if the percentage has ticked up by at least 1%
                if (currentPercent > lastNotifiedPercent) {
                    lastNotifiedPercent = currentPercent;
                    if (listener != null) {
                        listener.onProgressUpdate(currentPercent, totalBytesRead, totalSize);
                    }
                }
            }
        }
        return bytesReadThisTime;
    }
}
