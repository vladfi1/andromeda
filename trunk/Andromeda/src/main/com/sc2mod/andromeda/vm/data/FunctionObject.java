/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.vm.data;

import com.sc2mod.andromeda.environment.types.ClosureType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

public class FunctionObject extends DataObject{


	private FuncPointerDecl funcDecl;
	public FunctionObject(FuncPointerDecl func){
		funcDecl = func;
	}
	
	@Override
	public ExprNode getExpression() {
		return getLiteralExpr(LiteralTypeSE.INT);
	}

	@Override
	public IType getType() {
		return funcDecl.getType();
	}
	
	@Override
	public String toString() {
		return String.valueOf(funcDecl.getIndex());
	}
	
	@Override
	public boolean doNotInline() {
		return true;
	}

}
