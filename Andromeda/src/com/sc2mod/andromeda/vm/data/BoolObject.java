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
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;

public class BoolObject extends DataObject{
	
	private static final BoolObject TRUE = new BoolObject(true);
	private static final BoolObject FALSE = new BoolObject(false);
	private static LiteralExpression TRUE_LITERAL = TRUE.getLiteralExpr(LiteralType.BOOL);
	private static LiteralExpression FALSE_LITERAL = FALSE.getLiteralExpr(LiteralType.BOOL);
	
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
	public Expression getExpression() {
		if(val) return TRUE_LITERAL;
		return FALSE_LITERAL;
	}
	
	@Override
	public Type getType() {
		return BasicType.BOOL;
	}
	
	@Override
	public DataObject castTo(Type type) {
		switch(type.getRuntimeType()){
		case RuntimeType.BOOL: return this;
		case RuntimeType.STRING: return new StringObject(String.valueOf(val));
		case RuntimeType.TEXT: return new TextObject(String.valueOf(val));
			
		}
		return super.castTo(type);
	}
	
	
}
