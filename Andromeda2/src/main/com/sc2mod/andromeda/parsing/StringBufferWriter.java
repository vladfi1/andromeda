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

import java.io.IOException;

import com.sc2mod.andromeda.codegen.CodeWriter;

public class StringBufferWriter extends CodeWriter{

	private StringBuffer buffer = new StringBuffer(0);
	
	@Override
	public int getBytesOut() {
		return buffer.length();
	}

	@Override
	public void setCapacity(int length) {
		buffer.ensureCapacity(length);
	}

	@Override
	public void write(String toWrite) {
		buffer.append(toWrite);
	}

	@Override
	public void close() throws IOException {
		//String buffers don't have to be closed
	}
	
	@Override
	public String toString() {
		return buffer.toString();
	}

}
