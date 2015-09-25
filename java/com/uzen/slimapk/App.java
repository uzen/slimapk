package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.SlimApk;

class App {

	private static ApkOptions options;

	public static void main(String[] args) throws IOException {
		Action action = new Action(Action.PARSER);
		String[] path = parseArgs(args, action);
		if (path[0] == null || options == null) {
			return;
		} else if (path[1] == null) {
			path[1] = System.getProperty("user.dir");
		}
		
		switch(action.getType()) {
			case Action.PARSER:
				SlimParse ap = new SlimParse(path[0], path[1], options);
				ap.parse();
				break;
			case Action.INFO:
				SlimInfo ai = new SlimInfo(path[0], path[1], options);
				ai.info();
				break;
		} 
	}

	private static String[] parseArgs(final String[] args, Action action) {
		if (args.length < 1) {
			System.err.println(getSupport());
			return null;
		}

		String input = null, output = null;
		
		options = new ApkOptions();
		options.setABI(AndroidConstants.ABI_ARMv7);
		
		for (int i = 0; i < args.length; i++) {
			if (!args[i].startsWith("-")) {
				if (input == null) {
					input = args[i];
				} else if (output == null){
					output = args[i];
				} 
				continue;
			}

			if (args[i].startsWith("-l=")) {
				options.setFilesList(args[i].replaceFirst("-l=", ""));
				continue;
			}
			
			switch (args[i]) {
				case "-a32": case "--arm":
					//default ABI
					break;
				case "-a64": case "--arm64":
					options.setABI(AndroidConstants.ABI_ARMv8);
					break;
				case "-x86": case "--x86":
					options.setABI(AndroidConstants.ABI_X86);
					break;
				case "-x86_64": case "--x86_64":
					options.setABI(AndroidConstants.ABI_X86_64);
					break;
				case "-m": case "--multiple":
					options.setABI("multiple");
					break;
				case "-k": case "--keep-dir":
					options.setKeepMode(true);
					break;
				case "-i": case "--info":
					action.setType(Action.INFO);
					break;
				case "-d": case "--debug":
					options.setDebug(true);
					break;
				case "-ec": case "--enable-cache":
					options.setCacheStatus(true);
					break;
				default:
					System.err.println("Invalid argument: " + args[i]);
			}
		}
		
		return new String[]{input, output};
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
		+ " -x86_64 (--x86_64) - X86_64\n" 
		+ " -m (--multiple) : ARM, ARM64, X86, X86_64\n"
		+ " -k (--keep-directory) keep the structure the f/s\n" 
		+ " -l [-l=\"path to save the list of applications\"]\n" 
		+ " -i (--info) get more information about a specific package\n"
		+ "\nver. " + getImplementation() + "\n";
	}
}