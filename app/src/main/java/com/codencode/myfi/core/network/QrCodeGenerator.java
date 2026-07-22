package com.codencode.myfi.core.network;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public final class QrCodeGenerator {
    private static final int QR_CODE_SIZE_PX = 500;

    private QrCodeGenerator() {
    }

    public static Bitmap generate(String url) {
        Bitmap bitmap = Bitmap.createBitmap(
                QR_CODE_SIZE_PX,
                QR_CODE_SIZE_PX,
                Bitmap.Config.RGB_565
        );

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    url,
                    BarcodeFormat.QR_CODE,
                    QR_CODE_SIZE_PX,
                    QR_CODE_SIZE_PX
            );

            for (int x = 0; x < QR_CODE_SIZE_PX; x++) {
                for (int y = 0; y < QR_CODE_SIZE_PX; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
