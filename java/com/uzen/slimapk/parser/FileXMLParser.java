package com.uzen.slimapk.parser;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.resource.ResourceTable;
import com.uzen.slimapk.struct.exception.ParserException;
import com.uzen.slimapk.utils.Utils;

public class FileXMLParser {
	Path root;
	ResourceTable resourceTable;
	ApkMeta apkMeta;
	Locale locale = Locale.US;
	
	public void setName(Path root){
		this.root = root; 
	};
	
	public void parseData(){
		Path manifestXml = root.resolve(AndroidConstants.MANIFEST_FILE);
		XmlTranslator xmlTranslator = new XmlTranslator();
		ApkMetaTranslator translator = new ApkMetaTranslator();
		XmlStreamer xmlStreamer = new CompositeXmlStreamer(xmlTranslator, translator);
		try{
			transBinaryXml(manifestXml, xmlStreamer);
		} catch(IOException e) {
			e.printStackTrace();
		}
		if (xmlTranslator.getXml() == null)
      	throw new ParserException("manifest xml not exists");
      this.apkMeta = translator.getApkMeta();
   };

   private void transBinaryXml(Path manifestXml, XmlStreamer xmlStreamer) throws IOException {

        parseResourceTable();
        
        InputStream in = Files.newInputStream(manifestXml);
        ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
        BinaryXmlParser binaryXmlParser = new BinaryXmlParser(buffer, resourceTable);
        binaryXmlParser.setLocale(locale);
        binaryXmlParser.setXmlStreamer(xmlStreamer);
        binaryXmlParser.parse();
    }
    
    private void parseResourceTable() throws IOException {
        Path entry = root.resolve(AndroidConstants.RESOURCE_FILE);
        
        this.resourceTable = new ResourceTable();
        
        if (Files.notExists(entry))
            return;

        InputStream in = Files.newInputStream(entry);
        ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
        ResourceTableParser resourceTableParser = new ResourceTableParser(buffer);
        resourceTableParser.parse();
        this.resourceTable = resourceTableParser.getResourceTable();
    }
    
   public ApkMeta getMeta(){
		return apkMeta;
	};
}
