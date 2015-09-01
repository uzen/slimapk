package com.uzen.slimapk.Parser;

import java.util.regex.*;
import java.nio.file.*;
import java.io.IOException;

public class FileNameParser implements NameParser {
	private String name;
	// default tempale
	private Pattern pattern;
	
	public FileNameParser(String patt) {	
		if(patt == null || patt.isEmpty()) patt = "^(.+?)\\.apk";
		pattern = Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
	}
	public void setName(Path file) {
		this.name = file.getFileName().toString();
	}
	
	public void parseData() { //chrome.apk -> Chrome
		try {
			Matcher matcher = pattern.matcher(name);
			while (matcher.find()) {
				name = matcher.group(matcher.groupCount());
				name = name.toLowerCase();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
			}	
		} catch (PatternSyntaxException e) {
			System.out.printf("Invalid regular expression: %s\n (https://docs.oracle.com/javase/tutorial/essential/regex/)\n", e.getMessage());
			System.exit(2);
		}
	}
	public String getName() {
		return this.name;
	}
}