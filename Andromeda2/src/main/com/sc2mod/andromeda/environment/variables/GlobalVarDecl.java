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

import com.sc2mod.andromeda.environment.IGlobal;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class GlobalVarDecl extends NonParamDecl implements IGlobal{

	private FieldDeclaration declaration;
	private Scope scope;
	private int visibility;
	private int index;
	static int curIndex;
	
	public GlobalVarDecl(FieldDeclaration globalVarDeclaration, int index, Scope scope) {
		super(globalVarDeclaration.getFieldModifiers(),globalVarDeclaration.getType(),globalVarDeclaration.getDeclaredVariables().elementAt(index));
		this.declaration = globalVarDeclaration;
		this.scope = scope;
	}

	public SyntaxNode getDefinition() {
		return declaration;
	}

	@Override
	public Scope getScope() {
		return scope;
	}
	
	@Override
	public int getVisibility() {
		return visibility;
	}
	@Override
	public void setVisibility(int visibility) {
		this.visibility = visibility;		
	}

	@Override
	public int getDeclType() {
		return TYPE_GLOBAL;
	}
	
	@Override
	public int getIndex() {
		return index;
	}
	
	public boolean isGlobalField(){
		return true;
	}

	/**
	 * This method assigns the code index (its position in the source code) to the global variable.
	 * This may not be done in the constructor since globals get create before fields and
	 * this would screw up the order of global and class fields
	 */
	public void assignIndex() {
		this.index = curIndex++;

	}

}
