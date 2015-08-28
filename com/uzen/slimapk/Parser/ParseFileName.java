package com.uzen.slimapk.Parser;

import java.util.regex.*;
import java.nio.file.*;
import java.io.IOException;

public class ParseFileName implements ParseName {
	private String name;
	private String pattern = "^(.+?)\\.apk";
	
	public ParseFileName(String patt) {	// default tempale
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
		} catch (NullPointerException e) {}
	}
	public String getName() {
		return this.name;
	}
}