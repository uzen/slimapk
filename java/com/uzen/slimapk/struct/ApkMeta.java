package com.uzen.slimapk.struct;

/**
 * @author dongliu
 */
public class ApkMeta {

    private String packageName;
    private String label;
    private String versionName;
    private Long versionCode;
    private String minSdkVersion;
    private Boolean multiArch;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Long versionCode) {
        this.versionCode = versionCode;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }
    
    public Boolean getMultiArch() {
        return multiArch;
    }

    public void setMultiArch(Boolean multiArch) {
        this.multiArch = multiArch;
    }

    /**
     * alias for getLabel
     */
    public String getName() {
        return label;
    }

    /**
     * get the apk's title(name)
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
