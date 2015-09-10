package com.uzen.slimapk.parser;

import com.uzen.slimapk.struct.xml.*;

/**
 * callback interface for parse binary xml file.
 *
 * @author dongliu
 */
public interface XmlStreamer {

    void onStartTag(XmlNodeStartTag xmlNodeStartTag);

    void onEndTag(XmlNodeEndTag xmlNodeEndTag);
    
}
