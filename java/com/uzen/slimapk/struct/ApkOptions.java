package com.uzen.slimapk.struct;

public class ApkOptions {	
    
    private String abi;
    private String pathToFilesList;
    private boolean isDebug;
    private boolean isCache;
    private boolean keepMode;
    
    public void setABI(String abi) {
        this.abi = abi;
    } 
    
    public String getABI() {
        return abi;
    }
    
    public void setDebug(boolean debug) {
        this.isDebug = debug;
    }  
    
    public boolean isDebug() {
        return isDebug;
    }

    public void setCacheStatus(boolean status) {
        this.isCache = status;
    }  
    
    public boolean isCache() {
        return isCache;
    }

    public void setFilesList(String pathToFilesList) {
        this.pathToFilesList = pathToFilesList;
    }

    public String getFilesList() {
        return pathToFilesList;
    }

    public boolean getKeepMode() {
        return keepMode;
    } 
    
    public void setKeepMode(boolean keepMode) {
        this.keepMode = keepMode;
    } 
}
