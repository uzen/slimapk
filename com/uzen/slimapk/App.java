package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.SlimApk;

class App {

    public static void main(String[] args) throws IOException {
        ApkOptions opt = parseArgs(args);
        String in = args[args.length-2], out = args[args.length-1];
        try (SlimApk apk = new SlimApk(in, out, opt)) {
        		System.out.printf("Input file(directory): %s\nOutput directory: %s\nArchitecture: %s\n",
            in, out, opt.getType());
            System.out.println("#SlimApk");
            apk.parseDirectories();
            System.out.println("File search completed!");
        }
    }
    
    private static ApkOptions parseArgs(String[] args) {
        if (args.length < 2) SlimApk.debug(getSupport());

        String type = "arm", pattern = null;
        Boolean keepDir = false, speedMode = false, cleanMode = false;
        
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
         + " -a32 (--arm) - ARM architecture\n" 
         + " -a64 (--arm64) - ARM64 architecture\n" 
         + " -x86 (--x86) - X86 architecture\n" 
         + " -s (--save-directory) keep the structure the f/s\n" 
         + " -p (--pattern) [\"template for the file name\"]\n" 
         + " -h (--help) - help\n" 
         + "\nver. " + getImplementation() + "\n";
    }
}