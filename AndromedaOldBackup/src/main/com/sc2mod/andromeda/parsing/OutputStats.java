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

public class OutputStats {

	public final int globalsBytes;
	public final int stringLiteralBytes;
	public final int codeBytes;
	public OutputStats(int globalsBytes, int stringLiteralBytes, int codeBytes) {
		this.globalsBytes = globalsBytes;
		this.stringLiteralBytes = stringLiteralBytes;
		this.codeBytes = codeBytes;
	}
	
	public int getBytes(){
		return globalsBytes + stringLiteralBytes + codeBytes;
	}
}
