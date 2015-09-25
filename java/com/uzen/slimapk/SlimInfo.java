package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.io.IOException;

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
			Parser.setName(root);
			Parser.parseData();
			ApkMeta meta = Parser.getMeta();
			String[][] dump = {
				{"PackageName", meta.getName()},
				{"Version", meta.getVersionName()},
				{"minSdkVersion", meta.getMinSdkVersion()}, 
				{"VersionCode", String.valueOf(meta.getVersionCode())}
			};
			
			for(String[] data:dump){
				log.i(data[0] + ": " + data[1]);
			}
		} catch (IOException e) {
			log.e("Was unable to get information about a package:\n {0}", e);
		}
	}
}