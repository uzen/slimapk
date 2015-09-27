package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.SlimApk;
import com.uzen.slimapk.struct.exception.Log;

public class App {
	
	static Log log = new Log(App.class.getName());
	static String input;
	static String output;

	public static void main(String[] args) {
		if (args.length < 1) {
			log.i(getSupport());
			return;
		}
		
		Action action = new Action(Action.PARSER);
		ApkOptions options = parseArgs(args, action);
		if (input == null) {
			return;
		} else if (output == null) {
			output = System.getProperty("user.dir");
		}
		try{
			switch(action.getType()) {
				case Action.PARSER:
					SlimParser ap = new SlimParser(input, output, options);
					ap.parse();
					break;
				case Action.INFO:
					SlimInfo ai = new SlimInfo(input, output, options);
					ai.info();
					break;
			} 
		} catch (IOException e) {
			log.e(e.getMessage());
		}
	}

	private static ApkOptions parseArgs(final String[] args, Action action) {
		
		ApkOptions options = new ApkOptions();
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
					options.setABI(null);
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
					log.w("Invalid argument: " + args[i]);
			}
		}
		
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
		+ " -x86_64 (--x86_64) - X86_64\n" 
		+ " -m (--multiple) : ARM, ARM64, X86, X86_64\n"
		+ " -k (--keep-directory) keep the structure the f/s\n" 
		+ " -l [-l=\"path to save the list of applications\"]\n" 
		+ " -i (--info) get more information about a specific package\n"
		+ " -ec (--enable-catch) working with a copy of the application from the temporary folder\n"
		+ "\nver. " + getImplementation() + "\n";
	}
}