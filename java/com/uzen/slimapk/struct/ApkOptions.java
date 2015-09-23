package com.uzen.slimapk.struct;

public class ApkOptions {	
    private String abi;
    private String pathToFilesList;
    private Boolean isDebug;
    private String type;
    private String pattern;
    private Boolean keepMode;
    
    public void setABI(String abi) {
        this.abi = abi;
    }  
    
    public String getABI() {
        return abi;
    }
    
    public void setType(String type) {
        this.type = type;
    }  
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }  
    
    public void setKeepMode(Boolean keepMode) {
        this.keepMode = keepMode;
    }  
    
    public void setDebug(Boolean debug) {
        this.isDebug = debug;
    }  
    
    public Boolean isDebug() {
        return isDebug;
    }
    
    public String getType() {
        return type;
    }
    
    public String getPattern() {
        return pattern;
    }

    public void setFilesList(String pathToFilesList) {
        this.pathToFilesList = pathToFilesList;
    }

    public String getFilesList() {
        return pathToFilesList;
    }

    public Boolean getKeepMode() {
        return keepMode;
    }
}
