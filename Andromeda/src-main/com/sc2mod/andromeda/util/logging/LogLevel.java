package com.sc2mod.andromeda.util.logging;

public enum LogLevel {

	CAPTION(5,LogFormat.CAPTION),
	PHASE(2,LogFormat.NORMAL),
	DETAIL(1,LogFormat.NORMAL);
	
	
	
	private int level;
	private LogFormat format;
	
	private LogLevel(int level, LogFormat format){
		this.level = level;
		this.format = format;
	}
	
	int getLevel(){
		return level;
	}

	public LogFormat getFormat() {
		return LogFormat.NORMAL;
	}
}
