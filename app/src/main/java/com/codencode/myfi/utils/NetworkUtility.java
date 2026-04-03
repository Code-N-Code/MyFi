package com.codencode.myfi.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class NetworkUtility {
    public static String getHotspotIPAddress() {
        try {
            // Get all network interfaces (wlan0, eth0, ap0, etc.)
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface nInterface : Collections.list(interfaces)) {
                // We only care about interfaces that are UP and not the loopback (127.0.0.1)
                if (!nInterface.isUp() || nInterface.isLoopback()) {
                    continue;
                }

                // Look for common hotspot interface names
                // 'ap' stands for Access Point, 'wlan' for Wireless LAN
                String name = nInterface.getName().toLowerCase();
                if (name.contains("ap") || name.contains("wlan") || name.contains("softap")) {

                    Enumeration<InetAddress> addresses = nInterface.getInetAddresses();
                    for (InetAddress addr : Collections.list(addresses)) {
                        // We specifically want IPv4 (looks like 10.x.x.x or 192.x.x.x)
                        // and not the long IPv6 strings
                        if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') < 0) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "IP Not Found";
    }

    public static Bitmap generateQRCode(String url) {
        int width = 500;
        int height = 500;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Set pixels to Black or White based on the matrix
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
