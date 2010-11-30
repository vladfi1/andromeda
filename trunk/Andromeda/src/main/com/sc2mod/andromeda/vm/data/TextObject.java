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

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

public class TextObject extends DataObject{
	private final String val;
	
	public TextObject(String string) {
		this.val = string;
	}
	

	@Override
	public String toString() {
		return val;
	}

	@Override
	public ExprNode getExpression(TypeProvider tp) {
		return getLiteralExpr(tp, LiteralTypeSE.TEXT);
	}
	

	
	@Override
	public DataObject castTo(IType type) {
		if(type.getRuntimeType() == RuntimeType.TEXT) return this;
		return super.castTo(type);
	}
	
	
	@Override
	public IType getType(TypeProvider tp) {
		return tp.BASIC.TEXT;
	}
}
