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
import com.sc2mod.andromeda.syntaxNodes.Modifiers;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VariableDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarator;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclaratorId;

public abstract class NonParamDecl extends VarDecl {
	
	protected VariableDeclarator declarator;
	private boolean constant;
	private boolean isInitedField;
	
	@Override
	public void setConst() {
		constant = true;
	}
	
	@Override
	public boolean isConst() {
		return constant;
	}
	
	public NonParamDecl(Modifiers mods,Type type, VariableDeclarator decl){
		super(mods,type,decl.getName());
		this.declarator = decl;
		
		//Every field except variableDecls have an init
		if(!(declarator instanceof VariableDecl)){
			isInitedField = true;
		}
	}
	
	public NonParamDecl(Modifiers mods,com.sc2mod.andromeda.syntaxNodes.Type type,VariableDeclarator decl){
		super(mods,type,decl.getName());
		this.declarator = decl;
		declarator.setSemantics(this);
		
		//Every field except variableDecls have an init
		if(!(declarator instanceof VariableDecl)){
			isInitedField = true;
		}
	}
	
	public NonParamDecl(Modifiers fieldModifiers,
			com.sc2mod.andromeda.syntaxNodes.Type type,
			VariableDeclaratorId varDeclId) {
		super(fieldModifiers,type,varDeclId);
		this.declarator = null;
	}

	@Override
	public SyntaxNode getDefinition() {
		return declarator;
	}
	
	@Override
	public VariableDeclarator getDeclarator(){
		return declarator;
	}
	
	@Override
	public boolean isInitDecl() {
		return isInitedField;
	}
	
}
