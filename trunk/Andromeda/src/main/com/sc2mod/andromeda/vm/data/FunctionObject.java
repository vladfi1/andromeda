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

import com.sc2mod.andromeda.environment.access.OperationAccess;
import com.sc2mod.andromeda.environment.types.ClosureType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

public class FunctionObject extends DataObject{


	private OperationAccess funcDecl;
	public FunctionObject(OperationAccess func){
		funcDecl = func;
	}
	
	@Override
	public ExprNode getExpression(TypeProvider tp) {
		return getLiteralExpr(tp, LiteralTypeSE.INT);
	}

	@Override
	public IType getType(TypeProvider tp) {
		//TODO: What should this return for func objects?
		throw ErrorUtil.defaultInternalError();
		//return funcDecl.getType();
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
