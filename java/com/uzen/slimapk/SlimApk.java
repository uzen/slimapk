package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.exception.Log;

public abstract class SlimApk {
	protected ApkOptions Options;
	protected Path input, output;
	
	protected static AndroidConstants Const;	
	protected static Log log = new Log(SlimApk.class.getName());
	
	public SlimApk(String input, String output, ApkOptions Options) throws IOException {
		this.input = Paths.get(input);	
		if (Files.notExists(this.input)) 
			throw new IOException("No such file or directory: " + this.input);
		this.output = Paths.get(output);
		this.Options = Options;		
		
		if(Options.isDebug()) {
			log.isLoggable();
		}
	}
	
	abstract void build(Path path);
	
	public void search(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes fileAttributes) {
				if(file.toString().toLowerCase().endsWith(Const.EXTENSION)){
					build(file);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	public static Path createTempApp(Path source, Path temp) {
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
	
	public static void deleteDirectory(Path path) throws IOException {
		if (Files.notExists(path)){
			return;
		}
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes fileAttributes) {
				try {
					Files.delete(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return FileVisitResult.CONTINUE;
			};
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
				try {
					Files.delete(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				exc.printStackTrace();
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
