package com.uzen.slimapk;

import java.io.IOException;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.SlimApk;

class App {

    public static void main(String[] args) throws IOException {
        String[] path = new String[2];
        ApkOptions opt = parseArgs(args, path);
                		
        if(opt != null && path[0] != null) 
        try (SlimApk apk = new SlimApk(path[0], path[1], opt)) {
            System.out.println("#SlimApk\n" + apk.getConfig());
            apk.parseDirectories();
        }
    }
    
    private static ApkOptions parseArgs(final String[] args, String[] path) {
    	
        if (args.length < 1) {
            System.out.println(getSupport());
            return null;
        }

        String type = "arm", pattern = null;
        Boolean keepDir = false, cleanMode = false;
        
        for(int i=0; i < args.length; i++){
              if(!args[i].startsWith("-")){
        	if(path[0] == null){
        	 path[0] = args[i];
        	} else if(path[1] == null)
        	 path[1] = args[i];
        	continue;
              }
        		
         if(args[i].startsWith("-p=")){
        	pattern = args[i].replaceFirst("-p=","");
        	continue;
         }
        			 
          switch (args[i]){
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
               case "-h": case "--help":
                  System.out.println(getSupport());
                  return null;
               default:
               	System.out.println("Invalid argument: " +args[i]);
         }   
        }
        
        //remove after:
        cleanMode = null;	//(new func.)cleaning whole apk of extra libraries
        /*------------*/
        
        return new ApkOptions(type, pattern, keepDir, cleanMode);
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
         + " -k (--keep-directory) keep the structure the f/s\n" 
         + " -p [-p=\"template for the file name\"]\n" 
         + " -h (--help) - help\n" 
         + "\nver. " + getImplementation() + "\n";
    }
}
