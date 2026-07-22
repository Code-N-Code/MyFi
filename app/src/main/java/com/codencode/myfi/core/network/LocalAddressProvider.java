package com.codencode.myfi.core.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public final class LocalAddressProvider {
    private static final String IP_NOT_FOUND = "IP Not Found";

    private LocalAddressProvider() {
    }

    public static String getHotspotIpv4Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface networkInterface : Collections.list(interfaces)) {
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }

                String name = networkInterface.getName().toLowerCase();
                if (!name.contains("ap") && !name.contains("wlan") && !name.contains("softap")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                for (InetAddress address : Collections.list(addresses)) {
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(':') < 0) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return IP_NOT_FOUND;
    }
}
