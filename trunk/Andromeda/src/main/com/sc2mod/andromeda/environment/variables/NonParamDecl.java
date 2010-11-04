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

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;


public abstract class NonParamDecl extends VarDecl {
	
	protected VarDeclNode declarator;
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
	
	public NonParamDecl(ModifierListNode mods,IType type, VarDeclNode decl, IScope scope){
		super(mods,type,decl.getName(), scope);
		this.declarator = decl;
		
		//Every field except variableDecls have an init
		if(!(declarator instanceof UninitedVarDeclNode)){
			isInitedField = true;
		}
	}
	
	public NonParamDecl(ModifierListNode mods,IType type, IdentifierNode decl, IScope scope, boolean isInited){
		super(mods,type,decl, scope);
		isInitedField = isInited;
	}
	
	public NonParamDecl(ModifierListNode mods,com.sc2mod.andromeda.syntaxNodes.TypeNode type,VarDeclNode decl, IScope scope){
		super(mods,type,decl.getName(), scope);
		this.declarator = decl;
		
		//Every field except variableDecls have an init
		if(!(declarator instanceof UninitedVarDeclNode)){
			isInitedField = true;
		}
	}
	
	public NonParamDecl(ModifierListNode fieldModifiers,
			com.sc2mod.andromeda.syntaxNodes.TypeNode type,
			IdentifierNode varDeclId, IScope scope) {
		super(fieldModifiers,type,varDeclId, scope);
		this.declarator = null;
	}

	@Override
	public SyntaxNode getDefinition() {
		return declarator;
	}
	
	@Override
	public VarDeclNode getDeclarator(){
		return declarator;
	}
	
	@Override
	public boolean isInitDecl() {
		return isInitedField;
	}
	
}
