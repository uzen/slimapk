package com.uzen.slimapk.struct;

public class ApkOptions {	
    private String abi;

    private final String type;
    private final String pattern;
    private final Boolean keepMode;
    private final Boolean cleanMode;
    
    public ApkOptions(String type, String pattern, Boolean keepMode, Boolean cleanMode) {
        this.type = type;
        this.pattern = pattern;
        this.keepMode = keepMode;
        this.cleanMode = cleanMode;
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

    public Boolean getCleanMode() {
        return cleanMode;
    }

    public Boolean getKeepMode() {
        return keepMode;
    }
}
