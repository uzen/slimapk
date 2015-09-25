package com.uzen.slimapk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;

import com.uzen.slimapk.struct.AndroidConstants;

public class LibParser {

	private final Path LIB_DIR;
	private boolean hasMultiArch = false;	
	private static final String fs = System.getProperty("file.separator");
	private static final StandardCopyOption COPY_OPTION = StandardCopyOption.REPLACE_EXISTING;

	public LibParser(Path libdir, boolean hasMultiArch) {
		this.LIB_DIR = libdir;
		this.hasMultiArch = hasMultiArch;
	}
	
	public void extract(String abi, Path outdir) throws IOException {
		if (Files.notExists(LIB_DIR)) return;
		outdir = outdir.resolve(AndroidConstants.LIB_PREFIX);
		
		ArrayList<String> architectures = listFiles(LIB_DIR);
		Collections.sort(architectures);
		
		int archCount = architectures.size();
		
		if(abi == "multiple" || hasMultiArch) {
			for(int i = 0; i < archCount; i++){
				Path dir = LIB_DIR.resolve(architectures.get(i));
				copyFiles(dir, outdir.resolve(getType(architectures.get(i))));
			}
			return;
		}
		
		int index = architectures.indexOf(abi);
		
		if (index < 0) {
			index = architectures.indexOf(AndroidConstants.ABI_ARM);
		}
		
		if (index >= 0) {
			Path dir = LIB_DIR.resolve(architectures.get(index));
			copyFiles(dir, outdir.resolve(getType(architectures.get(index))));
		} else {
			throw new IOException("Incorrect architecture: " + abi);
		}
	}
	
	private String getType(String abi) {
		String arch = null;
		
		switch (abi) {
			case AndroidConstants.ABI_ARMv7:
				arch = AndroidConstants.ARM;
				break;
			case AndroidConstants.ABI_ARMv8:
				arch = AndroidConstants.ARM64;
				break;
			case AndroidConstants.ABI_X86:
				arch = AndroidConstants.X86;
				break;
			case AndroidConstants.ABI_X86_64:
				arch = AndroidConstants.X86_64;
				break;
			default:
				arch = AndroidConstants.ARM;
		}
		return arch;
	}
	
	public static ArrayList<String>listFiles(Path dir) throws IOException {
		ArrayList<String> list = new ArrayList<>();
		try (DirectoryStream < Path > stream = Files.newDirectoryStream(dir)) {
			for (Path file: stream) {
				list.add(getFileName(file));
		   }
		}
		return list;
   }	
   
	public static void copyFiles(Path dir, Path outdir) throws IOException {
		Files.createDirectories(outdir);
		try (DirectoryStream < Path > stream = Files.newDirectoryStream(dir)) {
			for (Path file: stream) {
				Files.copy(file, outdir.resolve(file.getFileName().toString()), COPY_OPTION);
			}
		}
	}
	
	private static String getFileName(Path file) {
		String path = file.toString();
		return path.substring(path.indexOf(fs, 2)+1, path.length()-1);
	}
}