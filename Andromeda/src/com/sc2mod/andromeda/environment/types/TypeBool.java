/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types;

public class TypeBool extends NonReferentialType{

	public TypeBool() {
		super("bool");
	}
	
	@Override
	public String getDefaultValueStr() {
		return "false";
	}
	
	@Override
	public int getRuntimeType() {
		return RuntimeType.BOOL;
	}
	
	@Override
	public int getByteSize() {
		return 1;
	}
	
}
