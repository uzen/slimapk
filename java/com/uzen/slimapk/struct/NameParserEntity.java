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

	private final Path root, apk;
	private final String pattern;
	private String name, version;

	public NameParserEntity(String pattern, Path root, Path apk) {
		this.pattern = pattern;
		this.root = root;
		this.apk = apk;
	}

	public void parse() {
		NameParser apkName;

		if (pattern != null) {
			apkName = new FileNameParser(pattern);
			apkName.setName(apk);
		} else {
			apkName = new FileXMLParser();
			apkName.setName(root);
		}

		apkName.parseData();

		ApkMeta apkMeta = apkName.getMeta();

		name = apkMeta.getName();
		if (name == null) {
			name = apk.getFileName().toString();
		}
		version = apkMeta.getVersionName();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
}