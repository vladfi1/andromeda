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

import com.sc2mod.andromeda.environment.scopes.Scope;


public abstract class SimpleType extends Type{

	protected SimpleType(Scope parentScope){
		super(parentScope);
	}
	
	public abstract String getName();
	
	protected String generatedName;
	
	@Override
	public String getGeneratedName() {
		return generatedName==null?getName():generatedName;
	}

	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}
}
