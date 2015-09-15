package com.uzen.slimapk.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.Map;

public class ResourceNote {

	public static Charset charsetUTF8 = Charset.forName("UTF-8");
	
	public static String queryString(Map <String, String> list) {
		StringBuilder sbuf = new StringBuilder();

		for (Map.Entry <String, String> entry: list.entrySet()) {
			sbuf.append(entry.getKey());
			sbuf.append(" #");
			sbuf.append(entry.getValue());
			sbuf.append('\n');
		}

		return sbuf.toString();
	}

	public static void writeToFile(Path file, Map <String, String> list) throws IOException {
		OutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = Files.newOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(queryString(list).getBytes(charsetUTF8));
		} finally {
			bos.flush();
			bos.close();
		}
	}
}