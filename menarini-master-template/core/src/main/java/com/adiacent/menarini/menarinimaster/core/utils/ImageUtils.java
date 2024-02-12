package com.adiacent.menarini.menarinimaster.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class ImageUtils {
    public static byte[] recoverImageFromUrl(String urlText) throws Exception {
        if(StringUtils.isBlank(urlText))
            return null;
        byte[] result = null;
        URL url = new URL(urlText);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int n = 0;
            byte [] buffer = new byte[ 1024 ];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
            if(inputStream != null)
                inputStream.close();
        }
        finally{
            if(output != null)
                output.close();
        }
        return result;
    }
}
