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
	
	//XPilot: these are necessary if we want bool -> string/text casts...
	/*
	@Override
	public boolean canExplicitCastTo(Type type) {
		if(super.canExplicitCastTo(type)) return true;
		return canConcatenateCastTo(type);
	}
	 
	@Override
	public boolean canConcatenateCastTo(Type toType) {
		if(toType == STRING || toType == TEXT) return true;
		return false;
	}
	*/
	
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
