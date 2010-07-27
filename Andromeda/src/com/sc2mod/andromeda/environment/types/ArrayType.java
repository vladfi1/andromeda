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

/**
 * An array type.
 * @author J. 'gex' Finis
 */
public class ArrayType extends Type {

	
	private Type wrappedType;
	private int dimension;
	private String uid;

	public ArrayType(Type wrappedType, int dimension){
		this.wrappedType = wrappedType;
		this.dimension = dimension;
		this.uid = new StringBuffer().append(wrappedType.getUid()).append("[").append(dimension).append("]").toString();
	}
	
	@Override
	public int getRuntimeType() {
		return RuntimeType.ARRAY;
	}
	
	@Override
	public int getCategory() {
		return ARRAY;
	}

	@Override
	public String getDescription() {
		return "array type";
	}

	@Override
	public String getUid() {
		return uid;
	}

	/**
	 * Arrays cannot be passed as parameter or returned
	 */
	@Override
	public boolean isValidAsParameter() {
		return false;
	}
	
	@Override
	public Type getWrappedType() {
		return wrappedType;
	}
	
	@Override
	public String getGeneratedName() {
		StringBuilder nameBuilder = new StringBuilder(32);
		nameBuilder.append(wrappedType.getGeneratedName()).append("[").append(dimension).append("]");
		return nameBuilder.toString();
	}
	
	@Override
	public int getByteSize() {
		//XPilot: changed from wrappedType.getByteSize()
		return wrappedType.getMemberByteSize()*dimension;
	}
}
