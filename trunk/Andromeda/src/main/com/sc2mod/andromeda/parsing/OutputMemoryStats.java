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

public class OutputMemoryStats {

	public final int globalsBytes;
	public final int stringLiteralBytes;
	public final int codeBytes;
	public final int bytesOut;
	
	public OutputMemoryStats(int globalsBytes, int stringLiteralBytes, int codeBytes, int outBytes) {
		this.globalsBytes = globalsBytes;
		this.stringLiteralBytes = stringLiteralBytes;
		this.codeBytes = codeBytes;
		this.bytesOut = outBytes;
	}
	
	public int getRuntimeMemoryUsage(){
		return globalsBytes + stringLiteralBytes + codeBytes;
	}
}
