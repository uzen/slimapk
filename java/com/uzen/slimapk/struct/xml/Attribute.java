package com.uzen.slimapk.struct.xml;

import com.uzen.slimapk.struct.ResourceEntity;
import com.uzen.slimapk.struct.resource.ResourceTable;

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
