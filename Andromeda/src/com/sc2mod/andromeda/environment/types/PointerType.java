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

public class PointerType extends Type {

	private Type pointsTo;
	private String uid;
	public PointerType(Type child) {
		pointsTo = child;
		uid = child.getUid() + "*";
	}

	@Override
	public int getCategory() {
		return POINTER;
	}

	@Override
	public String getDescription() {
		return "Pointer Type";
	}

	@Override
	public String getUid() {
		return uid;
	}
	
	@Override
	public String getGeneratedName() {
		return pointsTo.getGeneratedName().concat("*");
	}
	
	public Type pointsTo(){
		return pointsTo;
	}

	@Override
	public Type getWrappedType() {
		return pointsTo;
	}
	
	@Override
	public int getRuntimeType() {
		return RuntimeType.OTHER;
	}
	
	@Override
	public int getByteSize() {
		return 4;
	}
}
