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



public class SourceFileInfo {

	

	
	private final int fileId;
	private final InclusionType inclusionType;
	
	public int getFileId() {
		return fileId;
	}
	public InclusionType getInclusionType() {
		return inclusionType;
	}
	public SourceFileInfo(int fileId, InclusionType inclusionType, String importStr) {
		super();
		this.fileId = fileId;
		this.inclusionType = inclusionType;
	}
	
	
	
}
