package com.uzen.slimapk.struct;

/**
 * @author dongliu
 */
public class ApkMeta {

    private String packageName;
    private String label;
    private String versionName;
    private Long versionCode;

    private String targetSdkVersion;
    private String minSdkVersion;
    
    private boolean multiArch;

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
    
    public String getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public void setTargetSdkVersion(String targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
    }
	
    public boolean getMultiArch() {
        return multiArch;
    }

    public void setMultiArch(boolean multiArch) {
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
    
    @Override
    public String toString() {
        return "Package: \t" + packageName + "\n"
                + "PackageName: \t" + label + "\n"
                + "Version: \t" + versionName + "\n"
                + "VersionCode: \t" + versionCode + "\n"
                + "sdkVersion: \t" + targetSdkVersion + "\n"
                + "minSdkVersion: \t" + minSdkVersion + "\0";
    }
}
