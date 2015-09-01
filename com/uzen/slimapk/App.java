package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.SlimApk;

class App {
    
    private static String CURRENT_VERSION = null;
    
    private static String getImplementation() {
        Package pkg = App.class.getPackage();
        return pkg.getImplementationVersion();
    }
    
    private static String getSupport() {
        return "\nUsage: slimapk -i <INPUT_FOLDER/FILE> -o <OUTPUT_FOLDER>\n\n" 
         + "options:\n" + " -a32 (--arm) - ARM architecture\n" 
         + " -a64 (--arm64) - ARM64 architecture\n" 
         + " -x86 (--x86) - X86 architecture\n" 
         + " -s (--save-directory) keep the structure the f/s\n" 
         + " -h (--help) - help\n" + " -p (--pattern) [\"template for the file name\"]\n" 
         + "\nver. " + CURRENT_VERSION + "\n";
    }
    
    public static void main(String args[]) throws IOException {
        String input = null, output = null, arch = null, pattern = null;
        Boolean keepDir = false, speedMode = true;    
        
        CURRENT_VERSION = getImplementation();
        
        try {
            for (int i = 0; i < args.length; i++) {

                switch (args[i]) {
                    case "-i": case "--input-dir":
                        input = args[++i];
                        break;
                    case "-o": case "--output-dir":
                        output = args[++i];
                        break;
                    case "-p": case "--pattern":
                        pattern = args[++i];
                        break;
                    case "-a32": case "--arm":
                        arch = "arm";
                        break;
                    case "-a64": case "--arm64":
                        arch = "arm64";
                        break;
                    case "-x86": case "--x86":
                        arch = "x86";
                        break;
                    case "-s": case "--save":
                        keepDir = true;
                        break;
                    case "-h": case "--help":
                        getSupport();
                        break;
                    default:
                        SlimApk.debug("Invalid arguments");

                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            SlimApk.debug("You did not specify all the arguments");
        } catch (NullPointerException n) {
        		SlimApk.debug(getSupport());
        }

        if (input == null) getSupport();
        
        ApkOptions opt = new ApkOptions(arch, pattern, keepDir, speedMode, null); /*cleanMode*/
        try (SlimApk ApkFolder = new SlimApk(input, output, opt)) {
            System.out.printf("Input file/directory: %s\nOutput file/directory: %s\n",
            input, output);
            ApkFolder.unZipIt();
        }
    }
}
