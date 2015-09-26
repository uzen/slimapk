package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import com.uzen.slimapk.parser.ApkFileVisitor;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.exception.Log;
import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.utils.Utils;

public class SlimApk {
	protected ApkOptions Options;
	protected Path input, output, temp;
	
	protected static Log log = new Log(SlimApk.class.getName());
	
	private ArrayList<List<String>> List; //list of applications with versions
	
	public SlimApk(String input, String output, ApkOptions Options) throws IOException {
		this.input = FileSystems.getDefault().getPath(input);	
		if (Files.notExists(this.input)) 
			throw new IOException("No such file or directory: " + this.input);
		this.output = FileSystems.getDefault().getPath(output);
		this.Options = Options;		
		
		if(Options.isDebug()) {
			log.isLoggable();
			log.d("Input: {0}", this.input);		
			log.d("Output: {0}", this.output);
		}
		if(Options.getFilesList() != null) {	
			List = new ArrayList<>();
		}
		if(Options.isCache()){
			temp = Files.createTempDirectory("slim_");
		}
	}
	
	protected Path createTempApp(final Path source) {
		if(temp == null)
			return source;

		Path slim_apk = temp.resolve(source.getFileName());
		try{
			Files.copy(source, slim_apk);
		} catch(IOException e) {
			log.e("Error occurred while copying to a temporary directory:\n {0}", e);
			slim_apk = source;
		}
		return slim_apk;
	}
	
   protected void addElementToList(String label, String version) {
		if(List != null) {
			ArrayList<String> data = new ArrayList<>();
			data.add(label);
			data.add(version);
			List.add(data);
		}
	}
	
	public static void deleteDirectories(Path dir) throws IOException {
		ApkFileVisitor ApkLibDelParser = new ApkFileVisitor() {
			@Override
			public void actionDir(Path dir) {
				try {
					Files.delete(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
			@Override
			public void actionFile(Path file) {
				try {
					Files.delete(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		if (Files.exists(dir)) {
			Files.walkFileTree(dir, ApkLibDelParser);
		}
	}

	protected void writeFileList() throws IOException {
		if(List == null) return;
		Path file = FileSystems.getDefault().getPath(Options.getFilesList());
		log.d("Write a file list to {0}", file);
	   Utils.writeToFile(file, queryString(List));
	}
	
	protected static void cleaning(Path dir) throws IOException {
			deleteDirectories(dir);
			Files.createDirectories(dir);
	}
	
	private static String queryString(ArrayList<List<String>> list) {
		StringBuilder sbuf = new StringBuilder();

		for (List<String> entry: list) {
			int size = entry.size();
			for(int i = 0; i < size; i++){
				sbuf.append(entry.get(i));
				if(i == 0){
					sbuf.append(" #");
					continue;
				}
				sbuf.append(" ");
			}
			sbuf.append('\n');
		}

		return sbuf.toString();
	}
}
