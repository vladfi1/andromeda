/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen;

import java.io.Closeable;

public abstract class CodeWriter implements Closeable{

	public abstract void setCapacity(int length);
	
	public abstract int getBytesOut();
	
	public abstract void write(String toWrite);
	
}
