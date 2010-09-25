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

public class TypeString extends BasicType{

	public TypeString() {
		super("string");
	}
	
	 @Override
	public boolean canImplicitCastTo(Type toType) {
		 if(toType == this|| toType == TEXT) return true;
		 return false;
	}
	 
	 @Override
	public boolean canExplicitCastTo(Type type) {
		 return super.canExplicitCastTo(type) || canImplicitCastTo(type);
	}
	 
	 @Override
	public int getRuntimeType() {
		 return RuntimeType.STRING;
	 }


}
