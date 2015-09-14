package com.uzen.slimapk.parser;

import java.util.regex.*;
import java.nio.file.Path;
import java.io.IOException;

import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.struct.exception.ParserException;

public class FileNameParser implements NameParser {
	private Path file;
	// default tempale
	private String patt;
	private ApkMeta apkMeta;
	
	public FileNameParser(String spatt) {	
		if(spatt == null || spatt.isEmpty()){
			patt = "^(.+?)\\.apk";  //chrome.apk -> Chrome	
		} else patt = spatt;
	}
	
	public void setName(Path file) {
		this.file = file;
	}
	
	public void parseData() {
		String name = file.getFileName().toString();
		try{
			Pattern pattern = Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			
			if (matcher.find() && matcher.groupCount() > 0) {
				name = matcher.group(1);
			};
		} catch (PatternSyntaxException e) {
			throw new ParserException("Invalid regular expression: %s\n" + e.getMessage());
		} 
		
		name = name.toLowerCase();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		
		if(name == null) name = file.getFileName().toString();

		apkMeta.setLabel(name);
		apkMeta.setVersionName(null);
	}
	
	public ApkMeta getMeta() {
		return this.apkMeta;
	}
}