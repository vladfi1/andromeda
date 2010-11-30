package com.sc2mod.andromeda.parsing;


public enum InclusionType {

	/**
	 * The main map file(s) that are parsed
	 */
	MAIN(0), 
	
	/**
	 * Files that are included and are packed in the map file
	 */
	INCLUDE(1),

	/**
	 * Files that are included but are stored in the starcraft mpq files
	 */
	NATIVE(4),
	
	/**
	 * Andromeda library files. For these files, functions, types and variables 
	 * are only added to the compilation if they are actually called.
	 */
	LIBRARY(2),
	
	
	/**
	 * Language files from the lib/a/lang directors. These are always added.
	 */
	LANGUAGE(3);
	
	private int priority;
	private String description;
	
	public int getPriority(){
		return priority;
	}
	
	public String getDescription(){
		return description;
	}
	
	InclusionType(int priority, String description){
		this.priority = priority;
		this.description = description;
	}
	
	InclusionType(int priority){
		this(priority,null);
	}
	

	
}
