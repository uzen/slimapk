package com.uzen.slimapk.parser;

import com.uzen.slimapk.struct.exception.ParserException;
import com.uzen.slimapk.struct.ChunkHeader;
import com.uzen.slimapk.struct.ChunkType;
import com.uzen.slimapk.struct.StringPool;
import com.uzen.slimapk.struct.StringPoolHeader;
import com.uzen.slimapk.struct.resource.*;
import com.uzen.slimapk.utils.Buffers;
import com.uzen.slimapk.utils.ParseUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * parse android resource table file.
 * see http://justanapplication.wordpress.com/category/android/android-resources/
 */
public class ResourceTableParser {

    /**
     * By default the data buffer Chunks is buffer little-endian byte order both at runtime and when stored buffer files.
     */
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    private StringPool stringPool;
    private ByteBuffer buffer;
    // the resource table file size
    private ResourceTable resourceTable;

    private Set<Locale> locales;

    public ResourceTableParser(ByteBuffer buffer) {
        this.buffer = buffer.duplicate();
        this.buffer.order(byteOrder);
        this.locales = new HashSet<>();
    }

    /**
     * parse resource table file.
     */
    public void parse() {
        // read resource file header.
        ResourceTableHeader resourceTableHeader = (ResourceTableHeader) readChunkHeader();

        // read string pool chunk
        stringPool = ParseUtils.readStringPool(buffer, (StringPoolHeader) readChunkHeader());

        resourceTable = new ResourceTable();
        resourceTable.setStringPool(stringPool);

        PackageHeader packageHeader = (PackageHeader) readChunkHeader();
        
        ResourcePackage resourcePackage = new ResourcePackage(packageHeader);

        long beginPos = buffer.position();
        // read type string pool
        if (packageHeader.getTypeStrings() > 0) {
            buffer.position((int) (beginPos + packageHeader.getTypeStrings()
                    - packageHeader.getHeaderSize()));
            resourcePackage.setTypeStringPool(ParseUtils.readStringPool(buffer,
                    (StringPoolHeader) readChunkHeader()));
        }

        //read key string pool
        if (packageHeader.getKeyStrings() > 0) {
            buffer.position((int) (beginPos + packageHeader.getKeyStrings()
                    - packageHeader.getHeaderSize()));
            resourcePackage.setKeyStringPool(ParseUtils.readStringPool(buffer,
                    (StringPoolHeader) readChunkHeader()));
        }

        outer:
        while (buffer.hasRemaining()) {
            ChunkHeader chunkHeader = readChunkHeader();
            switch (chunkHeader.getChunkType()) {
                case ChunkType.TABLE_TYPE_SPEC:
                    long typeSpecChunkBegin = buffer.position();
                    TypeSpecHeader typeSpecHeader = (TypeSpecHeader) chunkHeader;

                    long[] entryFlags = new long[(int) typeSpecHeader.getEntryCount()];
                    for (int i = 0; i < typeSpecHeader.getEntryCount(); i++) {
                        entryFlags[i] = Buffers.readUInt(buffer);
                    }

                    TypeSpec typeSpec = new TypeSpec(typeSpecHeader);

                    typeSpec.setEntryFlags(entryFlags);
                    //id start from 1
                    typeSpec.setName(resourcePackage.getTypeStringPool()
                            .get(typeSpecHeader.getId() - 1));

                    resourcePackage.addTypeSpec(typeSpec);
                    buffer.position((int) (typeSpecChunkBegin + typeSpecHeader.getBodySize()));
                    break;
                case ChunkType.TABLE_TYPE:
                    long typeChunkBegin = buffer.position();
                    TypeHeader typeHeader = (TypeHeader) chunkHeader;
                    // read offsets table
                    long[] offsets = new long[(int) typeHeader.getEntryCount()];
                    for (int i = 0; i < typeHeader.getEntryCount(); i++) {
                        offsets[i] = Buffers.readUInt(buffer);
                    }

                    Type type = new Type(typeHeader);
                    type.setName(resourcePackage.getTypeStringPool().get(typeHeader.getId() - 1));
                    
                    long entryPos = typeChunkBegin + typeHeader.getEntriesStart()
                            - typeHeader.getHeaderSize();
                    buffer.position((int) entryPos);
                    ByteBuffer b = buffer.slice();
                    b.order(byteOrder);
                    type.setBuffer(b);
                    type.setKeyStringPool(resourcePackage.getKeyStringPool());
                    type.setOffsets(offsets);
                    type.setStringPool(stringPool);
                    resourcePackage.addType(type);
                    locales.add(type.getLocale());
                    buffer.position((int) (typeChunkBegin + typeHeader.getBodySize()));
                    break;
                case ChunkType.TABLE_PACKAGE:
                    break outer;
                default:
                    throw new ParserException("unexpected chunk type:" + chunkHeader.getChunkType());
            }
        }
        resourceTable.addPackage(resourcePackage);
    }
    
    private ChunkHeader readChunkHeader() {
        long begin = buffer.position();

        int chunkType = Buffers.readUShort(buffer);
        int headerSize = Buffers.readUShort(buffer);
        long chunkSize = Buffers.readUInt(buffer);

        switch (chunkType) {
            case ChunkType.TABLE:
                ResourceTableHeader resourceTableHeader = new ResourceTableHeader(chunkType,
                        headerSize, chunkSize);
                resourceTableHeader.setPackageCount(Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return resourceTableHeader;
            case ChunkType.STRING_POOL:
                StringPoolHeader stringPoolHeader = new StringPoolHeader(chunkType, headerSize,
                        chunkSize);
                stringPoolHeader.setStringCount(Buffers.readUInt(buffer));
                stringPoolHeader.setStyleCount(Buffers.readUInt(buffer));
                stringPoolHeader.setFlags(Buffers.readUInt(buffer));
                stringPoolHeader.setStringsStart(Buffers.readUInt(buffer));
                stringPoolHeader.setStylesStart(Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return stringPoolHeader;
            case ChunkType.TABLE_PACKAGE:
                PackageHeader packageHeader = new PackageHeader(chunkType, headerSize, chunkSize);
                packageHeader.setId(Buffers.readUInt(buffer));
                packageHeader.setName(ParseUtils.readStringUTF16(buffer, 128));
                packageHeader.setTypeStrings(Buffers.readUInt(buffer));
                packageHeader.setLastPublicType(Buffers.readUInt(buffer));
                packageHeader.setKeyStrings(Buffers.readUInt(buffer));
                packageHeader.setLastPublicKey(Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return packageHeader;
            case ChunkType.TABLE_TYPE_SPEC:
                TypeSpecHeader typeSpecHeader = new TypeSpecHeader(chunkType, headerSize, chunkSize);
                typeSpecHeader.setId(Buffers.readUByte(buffer));
                typeSpecHeader.setRes0(Buffers.readUByte(buffer));
                typeSpecHeader.setRes1(Buffers.readUShort(buffer));
                typeSpecHeader.setEntryCount(Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return typeSpecHeader;
            case ChunkType.TABLE_TYPE:
                TypeHeader typeHeader = new TypeHeader(chunkType, headerSize, chunkSize);
                typeHeader.setId(Buffers.readUByte(buffer));
                typeHeader.setRes0(Buffers.readUByte(buffer));
                typeHeader.setRes1(Buffers.readUShort(buffer));
                typeHeader.setEntryCount(Buffers.readUInt(buffer));
                typeHeader.setEntriesStart(Buffers.readUInt(buffer));
                typeHeader.setConfig(readResTableConfig());
                buffer.position((int) (begin + headerSize));
                return typeHeader;
            case ChunkType.NULL:
            default:
                throw new ParserException("Unexpected chunk Type:" + Integer.toHexString(chunkType));
        }
    }

    private ResTableConfig readResTableConfig() {
        long beginPos = buffer.position();
        ResTableConfig config = new ResTableConfig();
        long size = Buffers.readUInt(buffer);
        Buffers.skip(buffer, 4);
        //read locale
        config.setLanguage(new String(Buffers.readBytes(buffer, 2)).replace("\0", ""));
        config.setCountry(new String(Buffers.readBytes(buffer, 2)).replace("\0", ""));

        long endPos = buffer.position();
        Buffers.skip(buffer, (int) (size - (endPos - beginPos)));
        return config;
    }

    public ResourceTable getResourceTable() {
        return resourceTable;
    }

    public Set<Locale> getLocales() {
        return this.locales;
    }
}
