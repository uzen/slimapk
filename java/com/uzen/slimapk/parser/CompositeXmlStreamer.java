package com.uzen.slimapk.parser;

import com.uzen.slimapk.struct.xml.*;

/**
 * @author dongliu
 */
public class CompositeXmlStreamer implements XmlStreamer {

    public XmlStreamer[] xmlStreamers;

    public CompositeXmlStreamer(XmlStreamer... xmlStreamers) {
        this.xmlStreamers = xmlStreamers;
    }

    @Override
    public void onStartTag(XmlNodeStartTag xmlNodeStartTag) {
        for (XmlStreamer xmlStreamer : xmlStreamers) {
            xmlStreamer.onStartTag(xmlNodeStartTag);
        }
    }

    @Override
    public void onEndTag(XmlNodeEndTag xmlNodeEndTag) {
        for (XmlStreamer xmlStreamer : xmlStreamers) {
            xmlStreamer.onEndTag(xmlNodeEndTag);
        }
    }
}
