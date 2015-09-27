package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.io.IOException;

import com.uzen.slimapk.parser.FileXMLParser;
import com.uzen.slimapk.parser.ApkFileVisitor;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.utils.Utils;

public class SlimParser extends SlimApk {
	
	FileXMLParser parser;
	ApkMeta meta;	
	Path temp;

	public SlimParser(String input, String output, ApkOptions Options) throws IOException {
		super(input, output, Options);
		if(Options.isCache()){
			temp = Files.createTempDirectory("slim_");
		}
	}
	
	public void parse() {
		try {
			ApkFileVisitor ApkParser = new ApkFileVisitor() {
				@Override
				public void actionFile(Path apk) {
					if(apk.toString().toLowerCase().endsWith(AndroidConstants.EXTENSION)){
						unzipApk(apk);		
					}
				}
			};
			this.parser = new FileXMLParser();
			Files.createDirectories(output);
			Files.walkFileTree(input, ApkParser);
		} catch (IOException e) {
			log.e("Error while searching for files:\n {0}", e);
		} finally {
			try {
				if(temp != null) {
					temp.toFile().deleteOnExit();
				}
				writeFileList();
			} catch (IOException ex) {
				log.e("Error writing to the file list:\n {0}", ex);
			}
		}
	}
	
	private void unzipApk(Path source) {
		log.d("EXTRACTING: {0}", source);
		Path app = null, _app = createTempApp(source, temp);
		Path outdir = null;
		
		try {
			FileSystem ApkFileSystem = Utils.getFileSystem(_app);
			final Path root = ApkFileSystem.getPath("/");
			final Path libdir = root.resolve(AndroidConstants.LIB_PREFIX);
			meta = getMeta(root);
			String label = meta.getName(), version = meta.getVersionName();
			addElementToList(label, version);
			log.d("PackageName: " + label);
			log.d("Version: " + version);
			
			label = label.replaceAll("\\s","");
			
			if (Options.getKeepMode()) {
				outdir = output.resolve(input.relativize(source));
				outdir = outdir.resolveSibling(label);
			} else {
				outdir = output;
				outdir = outdir.resolve(label);
			}
						
			cleaning(outdir);			
			extractLibrary(libdir, outdir);
			ApkFileSystem.close();
			app = outdir.resolve(label + AndroidConstants.EXTENSION);
			Files.move(_app, app);				
			log.d("SAVE: {0}", app);
		} catch (IOException e) {
			log.e("Unpacking the application failed:\n {0}", e);
			try{
				if(outdir != null)
					deleteDirectories(outdir);
			} catch (IOException ex) {
				log.e("Сould not delete the application files:\n {0}", ex);
			}
		}
	}
	
	private void extractLibrary(Path libdir, Path outdir) throws IOException {
			LibraryFilter lib = new LibraryFilter(libdir, meta.getMultiArch());
			log.d("Copying libraries...");
			lib.extract(Options.getABI(), outdir);
			deleteDirectories(libdir);
	}
	
	private ApkMeta getMeta(Path path){
			parser.setName(path);
			parser.parseData();
			return parser.getMeta();
	}
}
