package com.codencode.myfi.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileUtilityTest {

    @Test
    public void formatSize_returnsBytesBelowOneKilobyte() {
        assertEquals("0 B", FileUtility.formatSize(0));
        assertEquals("1023 B", FileUtility.formatSize(1023));
    }

    @Test
    public void formatSize_formatsKilobytesAndMegabytesUsingTwoDecimals() {
        assertEquals("1.00 KB", FileUtility.formatSize(1024));
        assertEquals("1.50 KB", FileUtility.formatSize(1536));
        assertEquals("1.00 MB", FileUtility.formatSize(1024 * 1024));
    }
}
