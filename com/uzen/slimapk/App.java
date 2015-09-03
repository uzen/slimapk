package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.SlimApk;

class App {

    public static void main(String[] args) throws IOException {
        String[] path = new String[2];
        ApkOptions opt = parseArgs(args, path);
        try (SlimApk apk = new SlimApk(path[0], path[1], opt)) {
        		System.out.printf("Input file(directory): %s\nOutput directory: %s\nArchitecture: %s\n",
            path[0], path[1], opt.getType());
            System.out.println("#SlimApk");
            apk.parseDirectories();
            System.out.println("File search completed!");
        }
    }
    
    private static ApkOptions parseArgs(final String[] args, String[] path) {
        if (args.length < 2) SlimApk.debug(getSupport());

        String type = "arm", pattern = null;
        Boolean keepDir = false, speedMode = false, cleanMode = false;
        try{
	        for(int i=0; i < args.length; i++){
	        		if(!args[i].startsWith("-")) continue; 
	         	switch (args[i]){
	         		case "-i": case "--input":
	                   path[0] = args[++i];
	                   break;
	         		case "-o": case "--output":
	                   path[1] = args[++i];
	                   break;
	         		case "-p": case "--pattern":
	                   pattern = args[++i];
	                   break;
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
	               case "-s": case "--speed-mode":
	                   speedMode = true;
	                   break;
	               case "-h": case "--help":
	                   SlimApk.debug(getSupport());
	               default:
	                   SlimApk.debug("Invalid arguments");
	         	}   
	        }
        } catch (ArrayIndexOutOfBoundsException e) {
            SlimApk.debug("You did not specify all the arguments");
        }
        
        //remove after:
        speedMode = true;  //implementation FileXMLParser
        cleanMode = null;	//(new func.)cleaning whole apk of extra libraries
        /*------------*/
        
        return new ApkOptions(type, pattern, keepDir, speedMode, cleanMode);
    }
    
    private static String getImplementation() {
        Package pkg = App.class.getPackage();
        return pkg.getImplementationVersion();
    }
    
    private static String getSupport() {
        return "\nUsage: slimapk [options...] <INPUT_FOLDER/FILE> <OUTPUT_FOLDER>\n\n" 
         + "options:\n" 
         + " -a32 (--arm) - ARM\n" 
         + " -a64 (--arm64) - ARM 64-bit\n" 
         + " -x86 (--x86) - X86\n" 
         + " -s (--save-directory) keep the structure the f/s\n" 
         + " -p (--pattern) [\"template for the file name\"]\n" 
         + " -h (--help) - help\n" 
         + "\nver. " + getImplementation() + "\n";
    }
}