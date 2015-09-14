package com.uzen.slimapk.struct;

import java.nio.file.Path;

import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.parser.NameParser;
import com.uzen.slimapk.parser.FileNameParser;
import com.uzen.slimapk.parser.FileXMLParser;

/**
 * get name for apk file of androidmanifest.xml or her the name 
 */
public class NameParserEntity {

	private final NameParser apkName;
	private final String pattern;

	public NameParserEntity(String pattern) {
		this.pattern = pattern;
		if (pattern != null) {
			apkName = new FileNameParser(pattern);
		} else {
			apkName = new FileXMLParser();
		}
	}

	public String[] parse(final Path root, final Path apk) {
		if(pattern != null) {
			apkName.setName(apk);
		} else {
			apkName.setName(root);
		}
		apkName.parseData();
		
		ApkMeta meta = apkName.getMeta();
		
		return new String[]{ meta.getName(), meta.getVersionName() };
	}
}
