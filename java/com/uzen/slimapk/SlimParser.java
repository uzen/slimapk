package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.io.IOException;

import com.uzen.slimapk.parser.FileXMLParser;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.utils.Utils;

public class SlimParser extends SlimApk {

	private FileXMLParser parser;
	private FileCollections list;
	private Path temp;

	public SlimParser(String input, String output, ApkOptions Options) throws IOException {
		super(input, output, Options);
		this.parser = new FileXMLParser();
		
		log.d("Input: {0}", this.input);		
		log.d("Output: {0}", this.output);
		
		if(Options.isCache()){
			temp = Files.createTempDirectory("slim_");
		}
	}
	
	public void parse() {
		if(Options.getFilesList() != null) {	
			list = new FileCollections();
		}
		try {			
			Files.createDirectories(output);
			search(input);
		} catch (IOException e) {
			log.e("Error while searching for files:\n {0}", e);
		} finally {
			if(temp != null) {
				temp.toFile().deleteOnExit();
			}
			if(list != null) {	
				try{		
					log.d("Write a file list");
					Path fileList = Paths.get(Options.getFilesList());
					Utils.toFile(fileList, list.queryString(list.getList()));
				} catch (IOException ex) {
					log.e("Error writing to the file list:\n {0}", ex);
				}
			}
		}
	}
	
	public void build(Path source) {
		log.d("EXTRACTING: {0}", source);
		Path app = null, _app = createTempApp(source, temp);
		Path outdir = null;
		
		try {
			FileSystem ApkFileSystem = Utils.getFileSystem(_app);
			Path root = ApkFileSystem.getPath("/");
			ApkMeta meta = getAttribute(root);
			String label = meta.getLabel();
			if(list != null) 			
				list.addElementToList(new String[]{
					label,
					meta.getVersionName()
				});
			
			label = label.replaceAll("\\s","");
			
			if (Options.getKeepMode()) {
				outdir = output.resolve(input.relativize(source));
				outdir = outdir.resolveSibling(label);
			} else {
				outdir = output;
				outdir = outdir.resolve(label);
			}
			
			cleaningDirectory(outdir);			
			log.d("Copying libraries...");		
			extractLibrary(root.resolve(Const.LIB_PREFIX), outdir.resolve(Const.LIB_PREFIX), meta.getMultiArch());
			ApkFileSystem.close();
			app = outdir.resolve(label + Const.EXTENSION);
			Files.move(_app, app);				
			log.d("SAVE: {0}", app);
		} catch (IOException e) {
			log.e("Unpacking the application failed:\n {0}", e);
			try{
				deleteDirectory(outdir);
			} catch (IOException ex) {
				log.e("Сould not delete the application files:\n {0}", ex);
			}
		}
	}
	
	private void extractLibrary(Path libdir, Path outdir, boolean hasMultiArch) throws IOException {
			LibraryFilter lib = new LibraryFilter(libdir);
			String abi = Options.getABI();
			int index = lib.parse(abi);
			if(index >= 0) {
				if(abi == null || hasMultiArch) {
					lib.extractAll(outdir, index);
				} else {
					lib.extract(outdir, index);
				}				
				deleteDirectory(libdir);
			}
	}
	
	private ApkMeta getAttribute(Path path) {
			parser.setName(path);
			parser.parseData();
			return parser.getMeta();
	}	
	
	private void cleaningDirectory(Path dir) throws IOException {
			this.deleteDirectory(dir);
			Files.createDirectories(dir);
	}		
}
