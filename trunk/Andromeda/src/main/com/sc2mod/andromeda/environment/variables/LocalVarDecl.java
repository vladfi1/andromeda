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

import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class LocalVarDecl extends NonParamDecl {
	

	private boolean isOnTop;
	
	public LocalVarDecl(ModifierListNode mods,IType type, IdentifierNode decl, boolean isOnTop, IScope scope, boolean isInited){
		super(mods,type,decl,scope,isInited);
		this.isOnTop = isOnTop;
	}
	
	public LocalVarDecl(ModifierListNode mods,IType type, VarDeclNode decl, boolean isOnTop, IScope scope){
		super(mods,type,decl,scope);
		this.isOnTop = isOnTop;
	}
	
	public boolean isOnTop() {
		return isOnTop;
	}

	public void setOnTop(boolean isOnTop) {
		this.isOnTop = isOnTop;
	}
	
	@Override
	public String getGeneratedName() {
		if(overrides!=null) return overrides.getGeneratedName();
		return super.getGeneratedName();
	}

	

	@Override
	public VarType getVarType() {
		return VarType.LOCAL;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
