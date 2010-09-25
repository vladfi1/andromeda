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


public class SpecialType extends BasicType {

	public static SpecialType NULL = new TypeUnknown();
	public static SpecialType VOID = new SpecialType("void");
	
	
	protected SpecialType(String name) {
		super(name);
	}


	@Override
	public String getDescription() {
		return "void";
	}

	@Override
	public int getCategory() {
		return OTHER;
	}


	public static void registerSpecialTypes(TypeProvider t) {
		t.registerSimpleType(VOID);
		t.registerSimpleType(NULL);
	}
	

}
