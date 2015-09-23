package com.uzen.slimapk;

public class Action extends App {
	
	public static final int PARSER = 0x1;
	public static final int INFO = 0x2;
	
	public static int type;
	 
	Action(int type) {
		this.type = type;
	};
	
	public void setType(int type) {
		if(this.type < type)
			this.type = type;
	}
	
	public int getType() {
		return type;
	}
}