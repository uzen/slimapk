package com.uzen.slimapk.struct.xml;

import com.uzen.slimapk.struct.ResourceEntity;
import com.uzen.slimapk.struct.resource.ResourceTable;
import com.uzen.slimapk.utils.ResourceLoader;

import java.util.Locale;
import java.util.Map;

/**
 * xml node attribute
 *
 * @author dongliu
 */
public class Attribute {
    private String namespace;
    private String name;
    // The original raw string value of this 
    private String rawValue;
    // Processed typed value of this
    private ResourceEntity typedValue;
    // the final value as string
    private String value;

    public String toStringValue(ResourceTable resourceTable, Locale locale) {
        if (rawValue != null) {
            return rawValue;
        } else if (typedValue != null) {
            return typedValue.toStringValue(resourceTable, locale);
        } else {
            // something happen;
            return "";
        }
    }

    /**
     * These are attribute resource constants for the platform; as found in android.R.attr
     *
     * @author dongliu
     */
    public static class AttrIds {

        private static final Map<Integer, String> ids = ResourceLoader.loadSystemAttrIds();

        public static String getString(long id) {
            String value = ids.get((int) id);
            if (value == null) {
                value = "AttrId:0x" + Long.toHexString(id);
            }
            return value;
        }

    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public ResourceEntity getTypedValue() {
        return typedValue;
    }

    public void setTypedValue(ResourceEntity typedValue) {
        this.typedValue = typedValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                '}';
    }
}
