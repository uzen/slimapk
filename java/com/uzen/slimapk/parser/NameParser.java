package com.uzen.slimapk.parser;

import java.nio.file.Path; 
import com.uzen.slimapk.struct.ApkMeta;

public interface NameParser {

	void setName(Path file);
	
   void parseData();
		
	ApkMeta getMeta();

}	
