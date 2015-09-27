package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.io.IOException;

import com.uzen.slimapk.parser.FileXMLParser;
import com.uzen.slimapk.struct.ApkOptions;
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
			
			for(String value:dump(meta, libdir)) {
				log.i(value);
			}
			
		} catch (IOException e) {
			log.e("Was unable to get information about a package:\n {0}", e);
		}
	}
	
	private static ArrayList<String> dump(ApkMeta meta, Path path) {
		ArrayList<String> data = new ArrayList<>();
		data.add("Package: " + meta.getPackageName());
		data.add("Name: " + meta.getName());
		data.add("Version: " + meta.getVersionName());
		data.add("VersionCode: " + meta.getVersionCode());
		data.add("minSdkVersion: " + meta.getMinSdkVersion());
		data.add("native-code: " + meta.getMultiArch());
		String libs = new String();
		for(String entry:LibraryFilter.list(path))
			libs = libs.concat(" " + entry);
		if(libs.length() > 0)
			data.add("Library:" + libs);
			
		return data;
	}
}