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
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

public class LocalVarDecl extends NonParamDecl {
	

	private boolean isOnTop;
	
	public LocalVarDecl(ModifierListNode mods,Type type, VarDeclNode decl,  boolean isOnTop){
		super(mods,type,decl);
		this.isOnTop = isOnTop;
	}
	
	public boolean isOnTop() {
		return isOnTop;
	}

	public void setOnTop(boolean isOnTop) {
		this.isOnTop = isOnTop;
	}

	@Override
	public int getDeclType() {
		return TYPE_LOCAL;
	}
	
	@Override
	public String getGeneratedName() {
		if(overrides!=null) return overrides.getGeneratedName();
		return super.getGeneratedName();
	}

	
	
	
	
}
