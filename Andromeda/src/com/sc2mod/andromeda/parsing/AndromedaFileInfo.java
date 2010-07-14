/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

public class AndromedaFileInfo {

	/**
	 * The main map file(s) that are parsed
	 */
	public static final int TYPE_MAIN = 0; 
	
	/**
	 * Files that are included and are packed in the map file
	 */
	public static final int TYPE_INCLUDE = 1;

	/**
	 * Files that are included but are stored in the starcraft mpq files
	 */
	public static final int TYPE_NATIVE = 2;
	
	/**
	 * Andromeda library files. For these files, functions, types and variables 
	 * are only added to the compilation if they are actually called.
	 */
	public static final int TYPE_LIBRARY = 3;
	
	
	/**
	 * Language files from the lib/a/lang directors. These are always added.
	 */
	public static final int TYPE_LANGUAGE = 4;
	
	private int fileId;
	private int inclusionType;
	public int getFileId() {
		return fileId;
	}
	public int getInclusionType() {
		return inclusionType;
	}
	public AndromedaFileInfo(int fileId, int inclusionType, String importStr) {
		super();
		this.fileId = fileId;
		this.inclusionType = inclusionType;
	}
	
	
	
}
