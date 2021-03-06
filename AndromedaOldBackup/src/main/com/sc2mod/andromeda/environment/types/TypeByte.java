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

public class TypeByte extends NonReferentialType {

	public TypeByte() {
		super("byte");
	}
	
	@Override
	public String getDefaultValueStr() {
		return "0";
	}
	
	@Override
	public int getRuntimeType() {
		return RuntimeType.INT;
	}
	
	@Override
	public int getByteSize() {
		return 0;
	}
	
	public boolean isIntegerType(){
		return true;
	}
	
	@Override
	public boolean canImplicitCastTo(Type toType) {
		if(toType.getBaseType().isIntegerType()) return true;
		return super.canImplicitCastTo(toType);
	}
	
	@Override
	public Type getCommonSupertype(Type t) {
		Type base = t.getBaseType();
		if(base==BasicType.INT||base==BasicType.FLOAT) return t;
		return super.getCommonSupertype(t);
	}

}
