package com.uzen.slimapk.utils;

import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.net.URI;

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
    
    public static void writeToFile(Path file, String list) throws IOException {
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(file.toString());
    		fos.write(list.getBytes());
    		fos.flush();
    	} finally {
    		if(fos != null)
				fos.close();
    	}
    }
	
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    public static FileSystem getFileSystem(Path file) throws IOException {
        String encodeURI = file.toAbsolutePath().toString();
        encodeURI = encodeURI.replaceAll("\\s", "%20");
        URI uri = URI.create("jar:file:" + encodeURI);
		
        return FileSystems.newFileSystem(uri, new HashMap<String, String>());
    }
}
