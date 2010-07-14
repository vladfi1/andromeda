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

import com.sc2mod.andromeda.environment.types.FunctionPointer;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;

public class FunctionObject extends DataObject{


	private FuncPointerDecl funcDecl;
	public FunctionObject(FuncPointerDecl func){
		funcDecl = func;
	}
	
	@Override
	public Expression getExpression() {
		return getLiteralExpr(LiteralType.INT);
	}

	@Override
	public Type getType() {
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
