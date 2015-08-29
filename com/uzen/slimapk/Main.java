package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.SlimApk;

class Main {
	private static final String VERSION = "1.0.3";
	
	private static void usage(String msg) {
		System.out.println(msg);
		System.exit(1);
	}
	
	public static void main(String args[]) throws IOException {
		String input = null, output = null, arch = null, pattern = null;
		Boolean keepDir = false, speedMode = true;
		try {
			for (int i = 0; i < args.length; i++) {

				switch (args[i]) {
					case "-i":
					case "--input-dir":
						input = args[++i];
						break;
					case "-o":
					case "--output-dir":
						output = args[++i];
						break;
					case "-p":
					case "--pattern":
						pattern = args[++i];
						break;
					case "-a32":
					case "--arm":
						arch = "arm";
						break;
					case "-a64":
					case "--arm64":
						arch = "arm64";
						break;
					case "-x86":
					case "--x86":
						arch = "x86";
						break;
					case "-s":
					case "--save":
						keepDir = true;
						break;
					case "-h":
					case "--help":
						usage("Usage: slimapk -i <INPUT_FOLDER/FILE> -o <OUTPUT_FOLDER>\n" + "options:\n" + " -a32 (--arm) - ARM architecture\n" + " -a64 (--arm64) - ARM64 architecture\n" + " -x86 (--x86) - X86 architecture\n" + " -s (--save-directory) keep the structure the f/s\n" + " -h (--help) - help\n" + " -p (--pattern) [\"template for the file name\"]\n" + " -v (--version) - version\n");
						break;
					case "-v":
					case "--version":
						usage("ver. " + VERSION);
						break;
					default:
						usage("Invalid arguments");

				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			usage("You did not specify all the arguments");
		}

		if (input == null) usage("End.");

		try (SlimApk ApkFolder = new SlimApk(input, output, keepDir, pattern)) {
			ApkFolder.setType(arch);
			ApkFolder.setSpeedMode(speedMode);
			ApkFolder.unZipIt();
		}
	}
}