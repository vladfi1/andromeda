/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;

public class StaticInit extends Function {

	public StaticInit(StaticInitDeclaration functionDeclaration, Scope scope) {
		super(functionDeclaration, scope);
	}
	
	@Override
	public int getFunctionType() {
		return TYPE_STATIC_INIT;
	}

}
