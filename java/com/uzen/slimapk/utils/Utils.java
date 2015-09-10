package com.uzen.slimapk.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static byte[] toByteArray(InputStream in) throws IOException {
        try {
            byte[] buf = new byte[1024];
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int len;
                while ((len = in.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                return bos.toByteArray();
            }
        } finally {
            in.close();
        }
    }
    
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
