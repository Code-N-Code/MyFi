package com.codencode.myfi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class ProgressInputStreamTest {

    @Test
    public void read_reportsTransferredBytesAfterTheUpdateInterval() throws Exception {
        AtomicReference<ProgressUpdate> latestUpdate = new AtomicReference<>();
        ProgressInputStream stream = new ProgressInputStream(
                new ByteArrayInputStream(new byte[]{1, 2, 3, 4}),
                4,
                (percentage, bytesRead, totalSize, speedBytesPerSec) ->
                        latestUpdate.set(new ProgressUpdate(percentage, bytesRead, totalSize, speedBytesPerSec))
        );

        assertEquals(2, stream.read(new byte[2]));
        Thread.sleep(800);
        assertEquals(2, stream.read(new byte[2]));

        ProgressUpdate update = latestUpdate.get();
        assertNotNull(update);
        assertEquals(100, update.percentage);
        assertEquals(4, update.bytesRead);
        assertEquals(4, update.totalSize);
        assertTrue(update.speedBytesPerSecond >= 0);
    }

    private static final class ProgressUpdate {
        private final int percentage;
        private final long bytesRead;
        private final long totalSize;
        private final double speedBytesPerSecond;

        private ProgressUpdate(int percentage, long bytesRead, long totalSize, double speedBytesPerSecond) {
            this.percentage = percentage;
            this.bytesRead = bytesRead;
            this.totalSize = totalSize;
            this.speedBytesPerSecond = speedBytesPerSecond;
        }
    }
}
