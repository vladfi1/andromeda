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

public class StaticDecl extends SpecialType {

	private Type wrappedType;
	public StaticDecl(Type wrappedType) {
		super("type");
		this.wrappedType = wrappedType;
		
	}
	
	@Override
	public Type getWrappedType() {
		return wrappedType;
	}
	
	@Override
	public String getUid() {
		return "Type<" + wrappedType.getUid() + ">";
	}
	
	@Override
	public String getDescription() {
		return "Type Keyword";
	}
}
