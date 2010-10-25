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
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

public class StringObject extends DataObject{

	private final String val;
	
	public StringObject(String string) {
		this.val = string;
	}


	public StringObject(int i) {
		this.val = String.valueOf(i);
	}


	@Override
	public String toString() {
		return val;
	}

	
	@Override
	public ExprNode getExpression() {
		return getLiteralExpr(LiteralTypeSE.STRING);
	}
	

	
	@Override
	public DataObject castTo(Type type) {
		switch(type.getRuntimeType()){
		case RuntimeType.STRING: return this;
		case RuntimeType.TEXT: return new TextObject(val);
		case RuntimeType.INT: return new IntObject(Integer.parseInt(val));
		case RuntimeType.FLOAT: return new FixedObject(Float.parseFloat(val));
		}
		return super.castTo(type);
	}
	
	
	@Override
	public Type getType() {
		return BasicType.STRING;
	}
}
