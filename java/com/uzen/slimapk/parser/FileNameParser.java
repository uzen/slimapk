package com.uzen.slimapk.parser;

import java.util.regex.*;
import java.nio.file.*;
import java.io.IOException;

public class FileNameParser implements NameParser {
	private String name;
	// default tempale
	private String patt;
	
	public FileNameParser(String spatt) {	
		if(spatt == null || spatt.isEmpty()){
			patt = "^(.+?)\\.apk";  //chrome.apk -> Chrome	
		} else patt = spatt;
	}
	
	public void setName(Path file) {
		this.name = file.getFileName().toString();
	}
	
	public void parseData() {
		try{
			Pattern pattern = Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			
			if (matcher.find() && matcher.groupCount() > 0) {
				name = matcher.group(1);
			};
		} catch (PatternSyntaxException e) {
			System.out.printf("Invalid regular expression: %s\n", e.getMessage());
		} 
		
		name = name.toLowerCase();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public String getPattern() {
		return this.patt;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getVersion() {
		return null;
	}
}