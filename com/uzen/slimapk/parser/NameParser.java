package com.uzen.slimapk.parser;

import java.nio.file.Path; 

public interface NameParser {

	void setName(Path file);
	
   void parseData();
		
	String getName();
	
	String getVersion();

}	
