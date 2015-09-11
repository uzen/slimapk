package com.uzen.slimapk.utils;

import java.nio.file.Path;
import java.io.IOException;

import java.util.Map;

public class ResourceNote {
	
	private final Map<String, String> list;
	private final Path file;
	
	public ResourceNote(Path file, Map<String, String> list) {
		this.file = file;
		this.list = list;
	}
	
	public void writeToFile() {
		for(Map.Entry<String,String> m :list.entrySet()){
         System.out.println(m.getKey()+" : "+m.getValue());
      }

		//coming soon
	   try {
	   } catch (Exception e) {
	       e.printStackTrace();
	   } finally {
	   }
	}
}
