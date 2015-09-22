package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.SlimApk;

class App {

	public static void main(String[] args) throws IOException {
		String[] path = new String[2];
		ApkOptions options = parseArgs(args, path);
		if (path[0] == null || options == null) {
			return;
		} else if (path[1] == null) {
			path[1] = System.getProperty("user.dir");
		}
		try (SlimApk apk = new SlimApk(path[0], path[1], options)) {
			if(options.getPackageInfo()) {
				apk.info();
			} else {
				apk.parse();
			}
		}
	}

	private static ApkOptions parseArgs(final String[] args, String[] path) {

		if (args.length < 1) {
			System.err.println(getSupport());
			return null;
		}

		String type = "arm", pattern = null, pathToFilesList = null;
		Boolean keepDir = false, packageInfo = false;

		for (int i = 0; i < args.length; i++) {
			if (!args[i].startsWith("-")) {
				if (path[0] == null) {
					path[0] = args[i];
				} else if (path[1] == null){
					path[1] = args[i];
				} 
				continue;
			}

			if (args[i].startsWith("-p=")) {
				pattern = args[i].replaceFirst("-p=", "");
				continue;
			} else if (args[i].startsWith("-l=")) {
				pathToFilesList = args[i].replaceFirst("-l=", "");
				continue;
			}
			
			switch (args[i]) {
				case "-a32": case "--arm":
					type = "arm";
					break;
				case "-a64": case "--arm64":
					type = "arm64";
					break;
				case "-x86": case "--x86":
					type = "x86";
					break;
				case "-k": case "--keep-dir":
					keepDir = true;
					break;
				case "-i": case "--info":
					packageInfo = true;
					break;
				default:
					System.err.println("Invalid argument: " + args[i]);
			}
		}
		
		ApkOptions options = new ApkOptions(type, pattern, keepDir);
		options.setFilesList(pathToFilesList);
		options.setPackageInfo(packageInfo);		
		
		return options;
	}

	private static String getImplementation() {
		Package pkg = App.class.getPackage();
		return pkg.getImplementationVersion();
	}

	private static String getSupport() {
		return "Usage: slimapk [options...] <INPUT_FOLDER/FILE> <OUTPUT_FOLDER>\n" 
		+ "options:\n" 
		+ " -a32 (--arm) - ARM\n" 
		+ " -a64 (--arm64) - ARM 64-bit\n" 
		+ " -x86 (--x86) - X86\n" 
		+ " -k (--keep-directory) keep the structure the f/s\n" 
		+ " -p [-p=\"default template file name\"]\n" 
		+ " -l [-l=\"path to save the list of applications\"]\n" 
		+ " -i (--info) get more information about a specific package\n"
		+ "\nver. " + getImplementation() + "\n";
	}
}