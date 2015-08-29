package com.uzen.slimapk.Parser;

import java.nio.file.*; 

public interface NameParser {

	void setName(Path file);
		
	String getName();

   void parseData();

}	
