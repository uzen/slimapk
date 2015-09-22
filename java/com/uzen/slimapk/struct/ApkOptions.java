package com.uzen.slimapk.struct;

public class ApkOptions {	
    private String abi;
    private String pathToFilesList;
    private Boolean packageInfo;
    
    private final String type;
    private final String pattern;
    private final Boolean keepMode;
    
    
    public ApkOptions(String type, String pattern, Boolean keepMode) {
        this.type = type;
        this.pattern = pattern;
        this.keepMode = keepMode;
    }
    
    public void setABI(String abi) {
        this.abi = abi;
    }  
    
    public String getABI() {
        return abi;
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
    
    public void setPackageInfo(Boolean packageInfo) {
        this.packageInfo = packageInfo;
    }

    public Boolean getPackageInfo() {
        return packageInfo;
    }

    public Boolean getKeepMode() {
        return keepMode;
    }
}
