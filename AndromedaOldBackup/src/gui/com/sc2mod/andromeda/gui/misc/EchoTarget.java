/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.misc;

public interface EchoTarget {
	
	public static class EchoType{
		public static EchoType WARNING = new EchoType();
		private EchoType(){}
	}
	void print(String s, EchoType type);
}
