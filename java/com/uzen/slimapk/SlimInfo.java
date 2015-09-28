package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.io.IOException;

import com.uzen.slimapk.parser.FileXMLParser;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.utils.Utils;

public class SlimInfo extends SlimApk {

	public SlimInfo(String input, String output, ApkOptions Options) throws IOException {
		super(input, output, Options);
	}
	
	public void info() {
		getInfoPackage(input);
	}
	
	private void getInfoPackage(Path apk) {
		try (FileSystem ApkFileSystem = Utils.getFileSystem(apk)) {
			final Path root = ApkFileSystem.getPath("/");
			final Path libdir = root.resolve("lib");
			FileXMLParser Parser = new FileXMLParser();
			ApkMeta meta = new ApkMeta();
			
			Parser.setName(root);
			Parser.parseData();
			meta = Parser.getMeta();
			dump(meta, libdir);
			
		} catch (IOException e) {
			log.e("Was unable to get information about a package:\n {0}", e);
		}
	}
	
	private static void dump(ApkMeta meta, Path path) {
		char[] m_arr = meta.toString().toCharArray();

		for(int i = 0, x = 0; i < m_arr.length; i++) {
			if (m_arr[i] == '\n' || m_arr[i] == '\0') {
				char[] a = new char[i-x];
				System.arraycopy(m_arr, x, a, 0, i-x);
				x = i+1;
				log.i(String.valueOf(a));
			}
		}
		
		ArrayList<String> list = LibraryFilter.list(path);
		String str = new String("native-code: \t");
		String libs = new String();
		
		boolean outputAltNativeCode = false;
		
		if(meta.getMultiArch()) {
			int index = list.indexOf(AndroidConstants.ABI_X86_64);
			
			if (index < 0)
				index = list.indexOf(AndroidConstants.ABI_ARMv8);
				
			if (index >= 0) {	
				log.i(str + list.get(index));
				list.remove(index);
				outputAltNativeCode = true;
			}
		}
		
		for(String lib:list)
			libs = libs.concat(lib + " ");
			
		if(libs.length() > 0) {
			if(outputAltNativeCode)
				str = "alt-native: \t";
			log.i(str + libs);
		}
	}
}