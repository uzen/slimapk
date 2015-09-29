package com.uzen.slimapk;

import java.util.ArrayList;
import java.util.List;

public class FileCollections {

	private ArrayList<List<String>> List; //list of applications with versions
	
	public FileCollections() {
		List = new ArrayList<>();
	}
	
	private static List<String> toArrayList(String[] meta) {
		ArrayList<String> data = new ArrayList<>();
		for(String value:meta)
			data.add(value);
		return data;
	}
	
	public void addElementToList(String[] meta) {
		List.add(toArrayList(meta));
	}
	
	public static String queryString(ArrayList<List<String>> list) {
		StringBuilder sbuf = new StringBuilder();

		for (List<String> entry: list) {
			int size = entry.size(), i = 0;
			for(; i < size; i++){
				sbuf.append(entry.get(i));
				if(i == 0){
					sbuf.append(" #");
					continue;
				}
				sbuf.append(" ");
			}
			sbuf.append('\n');
		}

		return sbuf.toString();
	}
	
	public ArrayList<List<String>> getList() {
		return List;
	}
}