package com.uzen.slimapk.struct.xml;

import com.uzen.slimapk.struct.ChunkHeader;
/**
 * @author dongliu
 */
public class XmlNodeHeaderNull extends ChunkHeader {
    public XmlNodeHeaderNull(int chunkType, int headerSize, long chunkSize) {
        super(chunkType, headerSize, chunkSize);
    }
}
