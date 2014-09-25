package net.adamsmolnik.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ASmolnik
 *
 */
public interface Util {

    public static String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

}
