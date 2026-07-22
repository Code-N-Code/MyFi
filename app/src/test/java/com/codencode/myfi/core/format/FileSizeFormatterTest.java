package com.codencode.myfi.core.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileSizeFormatterTest {

    @Test
    public void format_returnsBytesBelowOneKilobyte() {
        assertEquals("0 B", FileSizeFormatter.format(0));
        assertEquals("1023 B", FileSizeFormatter.format(1023));
    }

    @Test
    public void format_formatsKilobytesAndMegabytesUsingTwoDecimals() {
        assertEquals("1.00 KB", FileSizeFormatter.format(1024));
        assertEquals("1.50 KB", FileSizeFormatter.format(1536));
        assertEquals("1.00 MB", FileSizeFormatter.format(1024 * 1024));
    }
}
