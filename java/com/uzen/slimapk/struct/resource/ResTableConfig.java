package com.uzen.slimapk.struct.resource;

/**
 * used by resource Type.
 *
 * @author dongliu
 */
public class ResTableConfig {
	
	 // 0 means "any".  Otherwise, en, fr, etc. char[2]
    private String language;
    // 0 means "any".  Otherwise, US, CA, etc.  char[2]
    private String country;
    
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
