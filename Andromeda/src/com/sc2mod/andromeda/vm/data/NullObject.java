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
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;

public class NullObject extends DataObject{
	
	public static final NullObject INSTANCE = new NullObject();
	private static final LiteralExpression NULL_LITERAL = INSTANCE.getLiteralExpr(LiteralType.NULL);
	private NullObject(){}
	
	@Override
	public String toString() {
		return "null";
	}

	
	@Override
	public Expression getExpression() {
		return NULL_LITERAL;
	}
	
	@Override
	public Type getType() {
		return SpecialType.NULL;
	}

	
	@Override
	public DataObject castTo(Type type) {
		return this;
	}

}
