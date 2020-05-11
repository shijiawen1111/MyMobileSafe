package com.example.mymobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    private static ByteArrayOutputStream baos;

    public static String parseStream(InputStream inputStream) {
        try {
            baos = new ByteArrayOutputStream();
            int len;
            byte[] arrays = new byte[1024];
            while ((len = inputStream.read(arrays)) != -1) {
                baos.write(arrays, 0, len);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(baos);
        }
        return null;
    }

    public static void closeIO(Closeable baos) {
        if (baos != null) {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        baos = null;
    }
}
