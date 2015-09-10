package com.uzen.slimapk.struct.resource;

import com.uzen.slimapk.struct.StringPool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dongliu
 */
public class ResourceTable {
    private Map<Short, ResourcePackage> packageMap = new HashMap<>();
    private StringPool stringPool;

    public void addPackage(ResourcePackage resourcePackage) {
        this.packageMap.put(resourcePackage.getId(), resourcePackage);
    }

    public ResourcePackage getPackage(short id) {
        return this.packageMap.get(id);
    }

    public StringPool getStringPool() {
        return stringPool;
    }

    public void setStringPool(StringPool stringPool) {
        this.stringPool = stringPool;
    }
}
