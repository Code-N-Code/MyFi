package com.codencode.myfi.utils;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FileUtilityInstrumentedTest {

    @Test
    public void getMimeType_returnsKnownTypesAndSafeFallback() {
        assertEquals("application/pdf", FileUtility.getMimeType("report.pdf"));
        assertEquals("image/jpeg", FileUtility.getMimeType("PHOTO.JPEG"));
        assertEquals("application/octet-stream", FileUtility.getMimeType("file-without-extension"));
    }
}
