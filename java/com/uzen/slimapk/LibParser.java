package com.uzen.slimapk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.exception.SlimLog;

public class LibParser {

	private final Path LIB_DIR;
	private String type, abi;
	
	private static final StandardCopyOption COPY_OPTION = StandardCopyOption.REPLACE_EXISTING;

	public LibParser(Path libdir, String type, String abi) {
		this.type = type;
		this.abi = abi;
		this.LIB_DIR = libdir;
	}
	
	public void extract(Path outdir) throws IOException {
		if (Files.notExists(LIB_DIR)) return;
		
		String[][] archTable = {
			{AndroidConstants.ABI_ARM,   AndroidConstants.ARM},
			{AndroidConstants.ABI_ARMv7, AndroidConstants.ARM}, 
			{AndroidConstants.ABI_ARMv8, AndroidConstants.ARM64}, 
			{AndroidConstants.ABI_X86,   AndroidConstants.X86},
		};
		
		if(type == "any") {
			for(String[] data:archTable){
				Path dir = checkType(data[0]);
				if(dir != null)
					copyFiles(dir, outdir.resolve(data[1]));
			}
		} else {
			Path dir = checkType(abi);
			if(dir != null) {
				copyFiles(dir, outdir.resolve(type));
			} else {
				Path _def = checkType(AndroidConstants.ABI_ARM);
				if(dir != null) {
					copyFiles(_def, outdir.resolve(AndroidConstants.ARM));
				} else
					throw new IOException("Incorrect architecture: " + abi);
			}
		}
	}

	private static void copyFiles(Path libdir, Path outdir) throws IOException {
		Files.createDirectories(outdir);
		try (DirectoryStream < Path > stream = Files.newDirectoryStream(libdir)) {
			for (Path file: stream) {
				Files.copy(file, outdir.resolve(file.getFileName().toString()), COPY_OPTION);
			}
		}
	}
	
	private Path checkType(String abi) {
		Path dir = LIB_DIR.resolve(abi);
		if (Files.notExists(dir)) 
			dir = null;
			
		return dir;
	}	
}