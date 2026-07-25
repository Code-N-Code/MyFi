package com.codencode.myfi.feature.send.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.codencode.myfi.feature.send.domain.ShareSession;
import com.codencode.myfi.feature.send.domain.TransferProgress;

import org.junit.Test;

public class SendUiStateTest {

    @Test
    public void retainsSessionStatusAndTransferInformation() {
        ShareSession session = new ShareSession(true, "http://192.168.1.5:8080", 3);
        TransferProgress progress = new TransferProgress(50, "1.00 MB/s");
        SendUiState state = new SendUiState(session, "Online (Transfer in Progress)", null, progress);

        assertTrue(state.getShareSession().isServerRunning());
        assertEquals("http://192.168.1.5:8080", state.getShareSession().getServerUrl());
        assertEquals(3, state.getShareSession().getSharedFileCount());
        assertEquals("Online (Transfer in Progress)", state.getStatusMessage());
        assertNull(state.getQrCode());
        assertEquals(50, state.getTransferProgress().getPercentage());
        assertEquals("1.00 MB/s", state.getTransferProgress().getSpeed());
    }
}
