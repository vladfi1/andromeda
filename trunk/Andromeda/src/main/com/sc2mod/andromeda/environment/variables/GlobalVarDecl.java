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
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class GlobalVarDecl extends NonLocalVarDecl implements IGlobal{

	private int index;
	
	//TODO static code smell
	static int curIndex;
	
	public GlobalVarDecl(FieldDeclNode globalVarDeclaration, VarDeclNode declNode, IScope scope) {
		super(globalVarDeclaration,declNode,scope);
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



	@Override
	public VarType getVarType() {
		return VarType.GLOBAL;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
