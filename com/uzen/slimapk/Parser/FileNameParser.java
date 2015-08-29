package com.uzen.slimapk.Parser;

import java.util.regex.*;
import java.nio.file.*;
import java.io.IOException;

public class FileNameParser implements NameParser {
	private String name;
	// default tempale
	private String pattern = "^(.+?)\\.apk";
	
	public FileNameParser(String patt) {	
		if(patt != null) this.pattern = patt;
	}
	public void setName(Path file) {
		this.name = file.getFileName().toString();
	}
	public void parseData() { //chrome.apk -> Chrome
		try {
			Matcher patt = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(name);
			if (patt.find()) {
				name = patt.group(1).toLowerCase();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	public String getName() {
		return this.name;
	}
}