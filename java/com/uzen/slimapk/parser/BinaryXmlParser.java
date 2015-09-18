package com.uzen.slimapk.parser;

import com.uzen.slimapk.struct.exception.ParserException;
import com.uzen.slimapk.struct.*;
import com.uzen.slimapk.struct.resource.ResourceTable;
import com.uzen.slimapk.struct.xml.*;
import com.uzen.slimapk.utils.Buffers;
import com.uzen.slimapk.utils.ParseUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

/**
 * Android Binary XML format
 * see http://justanapplication.wordpress.com/category/android/android-binary-xml/
 *
 * @author dongliu
 */
public class BinaryXmlParser {


    /**
     * By default the data buffer Chunks is buffer little-endian byte order both at runtime and when stored buffer files.
     */
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    private StringPool stringPool;
    // some attribute name stored by resource id
    private ByteBuffer buffer;
    private XmlStreamer xmlStreamer;
    private final ResourceTable resourceTable;
    /**
     * default locale.
     */
    private Locale locale = Locales.any;

    public BinaryXmlParser(ByteBuffer buffer, ResourceTable resourceTable) {
        this.buffer = buffer.duplicate();
        this.buffer.order(byteOrder);
        this.resourceTable = resourceTable;
    }

    /**
     * Parse binary xml.
     */
    public void parse() {
        ChunkHeader chunkHeader = readChunkHeader();
        if (chunkHeader == null) {
            return;
        }
        if (chunkHeader.getChunkType() != ChunkType.XML) {
            //TODO: may be a plain xml file.
            return;
        }
        XmlHeader xmlHeader = (XmlHeader) chunkHeader;

        // read string pool chunk
        chunkHeader = readChunkHeader();
        if (chunkHeader == null) {
            return;
        }
        ParseUtils.checkChunkType(ChunkType.STRING_POOL, chunkHeader.getChunkType());
        stringPool = ParseUtils.readStringPool(buffer, (StringPoolHeader) chunkHeader);
       	
        chunkHeader = readChunkHeader();

        while (chunkHeader != null) {
            long beginPos = buffer.position();
            switch (chunkHeader.getChunkType()) {
                case ChunkType.XML_START_ELEMENT:
                    XmlNodeStartTag xmlNodeStartTag = readXmlNodeStartTag();
                    break;
                case ChunkType.XML_END_ELEMENT:
                    XmlNodeEndTag xmlNodeEndTag = readXmlNodeEndTag();
                    break;
                default:
                    if (chunkHeader.getChunkType() >= ChunkType.XML_FIRST_CHUNK &&
                            chunkHeader.getChunkType() <= ChunkType.XML_RESOURCE_MAP) {
                        Buffers.skip(buffer, chunkHeader.getBodySize());
                    } else {
                    		throw new ParserException("Unexpected chunk:" + chunkHeader.getChunkType());
                    }
            }
            buffer.position((int) (beginPos + chunkHeader.getBodySize()));
            chunkHeader = readChunkHeader();
        }
    }

    private XmlNodeEndTag readXmlNodeEndTag() {
        XmlNodeEndTag xmlNodeEndTag = new XmlNodeEndTag();
        int nsRef = buffer.getInt();
        int nameRef = buffer.getInt();
        if (nsRef > 0) {
            xmlNodeEndTag.setNamespace(stringPool.get(nsRef));
        }
        xmlNodeEndTag.setName(stringPool.get(nameRef));
        if (xmlStreamer != null) {
            xmlStreamer.onEndTag(xmlNodeEndTag);
        }
        return xmlNodeEndTag;
    }

    private XmlNodeStartTag readXmlNodeStartTag() {
        int nsRef = buffer.getInt();
        int nameRef = buffer.getInt();
        XmlNodeStartTag xmlNodeStartTag = new XmlNodeStartTag();
        if (nsRef > 0) {
            xmlNodeStartTag.setNamespace(stringPool.get(nsRef));
        }
        xmlNodeStartTag.setName(stringPool.get(nameRef));

        // read attributes.
        // attributeStart and attributeSize are always 20 (0x14)
        int attributeStart = Buffers.readUShort(buffer);
        int attributeSize = Buffers.readUShort(buffer);
        int attributeCount = Buffers.readUShort(buffer);
        int idIndex = Buffers.readUShort(buffer);
        int classIndex = Buffers.readUShort(buffer);
        int styleIndex = Buffers.readUShort(buffer);

        // read attributes
        Attributes attributes = new Attributes(attributeCount);
        for (int count = 0; count < attributeCount; count++) {
            Attribute attribute = readAttribute();
            if (xmlStreamer != null) {
                String value = attribute.toStringValue(resourceTable, locale);
                attribute.setValue(value);
                attributes.set(count, attribute);
            }
        }
        xmlNodeStartTag.setAttributes(attributes);

        if (xmlStreamer != null) {
            xmlStreamer.onStartTag(xmlNodeStartTag);
        }

        return xmlNodeStartTag;
    }
    
    private Attribute readAttribute() {
        int nsRef = buffer.getInt();
        int nameRef = buffer.getInt();
        Attribute attribute = new Attribute();
        if (nsRef > 0) {
            attribute.setNamespace(stringPool.get(nsRef));
        }

        attribute.setName(stringPool.get(nameRef));

        int rawValueRef = buffer.getInt();
        if (rawValueRef > 0) {
            attribute.setRawValue(stringPool.get(rawValueRef));
        }
        ResourceEntity resValue = ParseUtils.readResValue(buffer, stringPool);
        attribute.setTypedValue(resValue);

        return attribute;
    }

    private ChunkHeader readChunkHeader() {
        // finished
        if (!buffer.hasRemaining()) {
            return null;
        }

        long begin = buffer.position();
        int chunkType = Buffers.readUShort(buffer);
        int headerSize = Buffers.readUShort(buffer);
        long chunkSize = Buffers.readUInt(buffer);

        switch (chunkType) {
            case ChunkType.XML:
                return new XmlHeader(chunkType, headerSize, chunkSize);
            case ChunkType.STRING_POOL:
                StringPoolHeader stringPoolHeader = new StringPoolHeader(chunkType, headerSize, chunkSize);
                stringPoolHeader.setStringCount(Buffers.readUInt(buffer));
                stringPoolHeader.setStyleCount(Buffers.readUInt(buffer));
                stringPoolHeader.setFlags(Buffers.readUInt(buffer));
                stringPoolHeader.setStringsStart(Buffers.readUInt(buffer));
                stringPoolHeader.setStylesStart(Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return stringPoolHeader;
            case ChunkType.XML_START_ELEMENT:
            case ChunkType.XML_END_ELEMENT:
                XmlNodeHeader header = new XmlNodeHeader(chunkType, headerSize, chunkSize);
                header.setLineNum((int) Buffers.readUInt(buffer));
                header.setCommentRef((int) Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return header;
            case ChunkType.XML_START_NAMESPACE:
            case ChunkType.XML_END_NAMESPACE:
            case ChunkType.XML_RESOURCE_MAP:
            case ChunkType.XML_CDATA:
                buffer.position((int) (begin + headerSize));
                return new XmlNodeHeaderNull(chunkType, headerSize, chunkSize);
            case ChunkType.NULL:
            default:
                throw new ParserException("Unexpected chunk type:" + chunkType);
        }
    }

    public void setLocale(Locale locale) {
        if (locale != null) {
            this.locale = locale;
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public XmlStreamer getXmlStreamer() {
        return xmlStreamer;
    }

    public void setXmlStreamer(XmlStreamer xmlStreamer) {
        this.xmlStreamer = xmlStreamer;
    }
}
