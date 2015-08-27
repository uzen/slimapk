package com.uzen.Parser;
import java.util.regex.*;
import java.nio.file.*;
import java.io.IOException;
public class ParseFileName implements ParseName {
	private String name;
	// default tempale
	private String pattern;
	public ParseFileName(String patt) {
		this.pattern = (patt != null) ? patt : "^(.+?)\\.apk";
	}
	public void setName(Path file) {
		this.name = file.getFileName().toString();
	}
	public void parseData() { //com.android.chrome.apk -> Chrome
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