/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNameNode;

public class ParamDecl extends VarDecl{

	public ParamDecl(ModifierListNode mods, Type type, VarDeclNameNode def) {
		super(mods, type, def);
	}

	@Override
	public int getDeclType() {
		return TYPE_PARAMETER;
	}

	@Override
	public boolean isInitDecl() {
		return false;
	}

}
