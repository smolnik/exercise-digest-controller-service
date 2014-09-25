package net.adamsmolnik.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ASmolnik
 *
 */
public class StreamReader {

    public static final int EOF = -1;

    private static final byte[] EMPTY = {};

    private StreamReader() {

    }

    public static byte[] getBytes(InputStream is) {
        return getBytes(is, EOF);
    }

    public static byte[] getBytes(InputStream is, int limit) {
        if (limit == 0) {
            return EMPTY;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        boolean limitApplied = limit > 0 ? true : false;

        int bufferLength = limitApplied && limit < buffer.length ? limit : buffer.length;
        try {
            while ((bytesRead = is.read(buffer, 0, bufferLength)) != -1) {
                int currentSize = bos.size();
                int futureSize = currentSize + bytesRead;
                if (limitApplied && limit < futureSize) {
                    bos.write(buffer, 0, limit - currentSize);
                    return bos.toByteArray();
                }
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return bos.toByteArray();
    }

}
