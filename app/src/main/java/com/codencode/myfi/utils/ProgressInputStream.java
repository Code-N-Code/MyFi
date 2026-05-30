package com.codencode.myfi.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends FilterInputStream {
    private long totalBytesRead = 0;
    private final long totalSize;
    private int lastNotifiedPercent = -1;
    private final ProgressListener listener;
    
    private long startTime = -1;
    private long lastTime = -1;
    private long lastBytes = 0;

    public interface ProgressListener {
        void onProgressUpdate(int percentage, long bytesRead, long totalSize, double speedBytesPerSec);
    }

    public ProgressInputStream(InputStream in, long totalSize, ProgressListener listener) {
        super(in);
        this.totalSize = totalSize;
        this.listener = listener;
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b != -1) {
            updateProgress(1);
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesReadThisTime = super.read(b, off, len);
        if (bytesReadThisTime != -1) {
            updateProgress(bytesReadThisTime);
        }
        return bytesReadThisTime;
    }

    private void updateProgress(int bytesReadNow) {
        long currentTime = System.currentTimeMillis();
        if (startTime == -1) {
            startTime = currentTime;
            lastTime = currentTime;
        }

        totalBytesRead += bytesReadNow;

        // Calculate progress percentage
        int currentPercent = (totalSize > 0) ? (int) ((totalBytesRead * 100) / totalSize) : 0;

        // Notify if percentage changed OR if a significant amount of time has passed (e.g. 500ms) for speed updates
        long timeDiff = currentTime - lastTime;
        if (timeDiff >= 750) {
            
            double speed = (totalBytesRead - lastBytes) / (timeDiff / 1000.0);

            lastNotifiedPercent = currentPercent;
            lastTime = currentTime;
            lastBytes = totalBytesRead;

            if (listener != null) {
                listener.onProgressUpdate(currentPercent, totalBytesRead, totalSize, speed);
            }
        }
    }
}
