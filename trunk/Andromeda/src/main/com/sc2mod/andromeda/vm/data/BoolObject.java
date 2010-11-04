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

import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

public class BoolObject extends DataObject{
	
	private static final BoolObject TRUE = new BoolObject(true);
	private static final BoolObject FALSE = new BoolObject(false);
	private static LiteralExprNode TRUE_LITERAL = TRUE.getLiteralExpr(LiteralTypeSE.BOOL);
	private static LiteralExprNode FALSE_LITERAL = FALSE.getLiteralExpr(LiteralTypeSE.BOOL);
	
	public static BoolObject getBool(boolean val)
	{
		if(val) return TRUE;
		return FALSE;
	}
	
	private boolean val;

	private BoolObject(boolean val) {
		super();
		this.val = val;
	}
	
	@Override
	public String toString() {
		return String.valueOf(val);
	}
	
	@Override
	public ExprNode getExpression() {
		if(val) return TRUE_LITERAL;
		return FALSE_LITERAL;
	}
	
	@Override
	public IType getType() {
		return BasicType.BOOL;
	}
	
	@Override
	public DataObject castTo(IType type) {
		switch(type.getRuntimeType()){
		case RuntimeType.BOOL: return this;
		case RuntimeType.STRING: return new StringObject(String.valueOf(val));
		case RuntimeType.TEXT: return new TextObject(String.valueOf(val));
			
		}
		return super.castTo(type);
	}
	
	
}
