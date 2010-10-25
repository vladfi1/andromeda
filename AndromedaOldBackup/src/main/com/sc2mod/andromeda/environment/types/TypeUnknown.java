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

public class TypeUnknown extends SpecialType {

	public TypeUnknown() {
		super("unknown");
	}
	
	@Override
	public boolean canImplicitCastTo(Type toType) {
		if(canBeNull()) return true;
		return false;
	}
	
	@Override
	public boolean canExplicitCastTo(Type type) {
		return canImplicitCastTo(type);
	}
	
	
	@Override
	public String getDescription() {
		return "unknown (null)";
	}
}
