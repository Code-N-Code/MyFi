package com.codencode.myfi.core.format;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MimeTypeResolverInstrumentedTest {

    @Test
    public void fromFileName_returnsKnownTypesAndSafeFallback() {
        assertEquals("application/pdf", MimeTypeResolver.fromFileName("report.pdf"));
        assertEquals("image/jpeg", MimeTypeResolver.fromFileName("PHOTO.JPEG"));
        assertEquals("application/octet-stream", MimeTypeResolver.fromFileName("file-without-extension"));
    }
}
