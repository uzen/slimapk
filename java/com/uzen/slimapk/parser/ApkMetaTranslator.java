package com.uzen.slimapk.parser;

import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.struct.xml.*;

/**
 * trans binary xml to text
 *
 * @author Liu Dong im@dongliu.net
 */
public class ApkMetaTranslator implements XmlStreamer {
    private String[] tagStack = new String[100];
    private int depth = 0;
    private ApkMeta apkMeta = new ApkMeta();

    @Override
    public void onStartTag(XmlNodeStartTag xmlNodeStartTag) {
        Attributes attributes = xmlNodeStartTag.getAttributes();
        switch (xmlNodeStartTag.getName()) {
            case "application":
                apkMeta.setLabel(attributes.get("label"));
                break;
            case "manifest":
                apkMeta.setPackageName(attributes.get("package"));
                apkMeta.setVersionName(attributes.get("versionName"));
                apkMeta.setVersionCode(attributes.getLong("versionCode"));
                break;
            case "uses-sdk":
                apkMeta.setMinSdkVersion(attributes.get("minSdkVersion"));
                break;
        }
        tagStack[depth++] = xmlNodeStartTag.getName();
    }

    @Override
    public void onEndTag(XmlNodeEndTag xmlNodeEndTag) {
        depth--;
    }

    @Override
    public void onCData(XmlCData xmlCData) {

    }

    @Override
    public void onNamespaceStart(XmlNamespaceStartTag tag) {

    }

    @Override
    public void onNamespaceEnd(XmlNamespaceEndTag tag) {

    }

    public ApkMeta getApkMeta() {
        return apkMeta;
    }

    private boolean matchTagPath(String... tags) {
        // the root should always be "manifest"
        if (depth != tags.length + 1) {
            return false;
        }
        for (int i = 1; i < depth; i++) {
            if (!tagStack[i].equals(tags[i - 1])) {
                return false;
            }
        }
        return true;
    }

    private boolean matchLastTag(String tag) {
        // the root should always be "manifest"
        return tagStack[depth - 1].endsWith(tag);
    }
}
