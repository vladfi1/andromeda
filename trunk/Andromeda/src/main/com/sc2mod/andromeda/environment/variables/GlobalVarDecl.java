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

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.IGlobal;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class GlobalVarDecl extends NonLocalVarDecl implements IGlobal{

		
	public GlobalVarDecl(FieldDeclNode globalVarDeclaration, VarDeclNode declNode, IScope scope, Environment env) {
		super(globalVarDeclaration,declNode,scope, env);
	}

	
	public boolean isGlobalField(){
		return true;
	}

	@Override
	public String getDescription() {
		return "global variable " + getUid();
	}


	@Override
	public VarType getVarType() {
		return VarType.GLOBAL;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
