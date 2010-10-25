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
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class GlobalVarDecl extends NonParamDecl implements IGlobal{

	private VarDeclNode declaration;
	private Scope scope;
	private int index;
	static int curIndex;
	
	public GlobalVarDecl(FieldDeclNode globalVarDeclaration, VarDeclNode declNode, Scope scope) {
		super(globalVarDeclaration.getFieldModifiers(),globalVarDeclaration.getType(),declNode,scope);
		this.declaration = declNode;
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
	public void setVisibility(Visibility visibility) {
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

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
