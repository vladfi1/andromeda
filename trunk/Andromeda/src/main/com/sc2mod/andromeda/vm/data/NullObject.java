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

import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

public class NullObject extends DataObject{
	
	public static final NullObject INSTANCE = new NullObject();
	private static final LiteralExprNode NULL_LITERAL = INSTANCE.getLiteralExpr(LiteralTypeSE.NULL);
	private NullObject(){}
	
	@Override
	public String toString() {
		return "null";
	}

	
	@Override
	public ExprNode getExpression() {
		return NULL_LITERAL;
	}
	
	@Override
	public IType getType() {
		return SpecialType.NULL;
	}

	
	@Override
	public DataObject castTo(IType type) {
		return this;
	}

}
