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

public class TypeInt extends NonReferentialType{

	public TypeInt() {
		super("int");
	}
	
	 @Override
	public boolean canImplicitCastTo(Type toType) {
		 if(toType == this|| toType == FLOAT) return true;
		 return false;
	}
	 
	 @Override
	public boolean canExplicitCastTo(Type type) {
		 if(super.canExplicitCastTo(type)) return true;
		 if(type.getBaseType()==BasicType.BYTE) return true;
		 if(type.getCategory()==CLASS) return true;	
		 if(type.getCategory()==TYPE_PARAM) return true;
		 return canConcatenateCastTo(type);
	}
	 
	 @Override
	public boolean canConcatenateCastTo(Type toType) {
		if(toType == this|| toType == FLOAT || toType == STRING || toType == TEXT) return true;
		return false;
	}
	 
	 @Override
	public String getDefaultValueStr() {
		return "0";
	}
	 
	 @Override
	public int getRuntimeType() {
		 return RuntimeType.INT;
	}

	public boolean isIntegerType(){
		return true;
	}
	
	@Override
	public Type getCommonSupertype(Type t) {
		Type base = t.getBaseType();
		if(base==BasicType.FLOAT) return t;
		return super.getCommonSupertype(t);
	}
}
