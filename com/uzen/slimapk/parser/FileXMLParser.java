package com.uzen.slimapk.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.util.*;

/*
import com.uzen.slimapk.bean.*;
import com.uzen.slimapk.exception.ParserException;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.resource.ResourceTable;
import com.uzen.slimapk.utils.Utils;
import com.uzen.slimapk.parser.*;
*/

public class FileXMLParser implements NameParser {
	
	private Path root;
	/*
	private ResourceTable resourceTable;
	private Set<Locale> locales;
	private Locale preferredLocale = Locale.US;
	private ApkMeta apkMeta;
	*/
	public void setName(Path root){
		this.root = root; 
	};
	
	public void parseData(){
		/*
   	Path manifestXml = root.resolve(AndroidConstants.MANIFEST_FILE);
		XmlTranslator xmlTranslator = new XmlTranslator();
		ApkMetaTranslator translator = new ApkMetaTranslator();
		XmlStreamer xmlStreamer = new CompositeXmlStreamer(xmlTranslator, translator);
		try{
			transBinaryXml(manifestXml, xmlStreamer);
		} catch(IOException e){
		
		}
		String manifest = xmlTranslator.getXml();
		if (manifest == null) {
        throw new ParserException("manifest xml not exists");
      }
      this.apkMeta = translator.getApkMeta();
      */
   };
   
   
	 /*private void transBinaryXml(Path manifestXml, XmlStreamer xmlStreamer) throws IOException {
    
     if (this.resourceTable == null) {
         parseResourceTable();
     }
     
     InputStream in = Files.newInputStream(manifestXml);
     ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
     BinaryXmlParser binaryXmlParser = new BinaryXmlParser(buffer, resourceTable);
     binaryXmlParser.setLocale(preferredLocale);
     binaryXmlParser.setXmlStreamer(xmlStreamer);
     binaryXmlParser.parse();
     
	}*/
     
	private void parseResourceTable() throws IOException {
	/*
     Path entry = root.resolve(AndroidConstants.RESOURCE_FILE);
     if (Files.notExists(entry)) {
         // if no resource entry has been found, we assume it is not needed by this APK
         this.resourceTable = new ResourceTable();
         this.locales = Collections.emptySet();
         return;
     }

     this.resourceTable = new ResourceTable();
     this.locales = Collections.emptySet();

     InputStream in = Files.newInputStream(entry);
     ByteBuffer buffer = ByteBuffer.wrap(Utils.toByteArray(in));
     ResourceTableParser resourceTableParser = new ResourceTableParser(buffer);
     resourceTableParser.parse();
     this.resourceTable = resourceTableParser.getResourceTable();
     this.locales = resourceTableParser.getLocales();
   */
	}
   
   
	public String getName() {
		return null; //apkMeta.getName();
	};

   
   public String getVersion() {
   	return null; //apkMeta.getVersionName();
   };   
}
