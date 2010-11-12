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

import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

public class IntObject extends DataObject {

	private int val;
	
	public IntObject(int result) {
		this.val = result;
	}
	
	@Override
	public String toString() {
		return String.valueOf(val);
	}

	@Override
	public ExprNode getExpression(TypeProvider tp) {
		return getLiteralExpr(tp,LiteralTypeSE.INT);
	}
	
	@Override
	public int getIntValue() {
		return val;
	}
	
	@Override
	public Fixed getFixedValue() {
		return Fixed.fromInt(val);
	}
	
	@Override
	public DataObject castTo(IType type) {
		switch(type.getRuntimeType()){
		case RuntimeType.INT:{
			if(type.getBaseType() == type.getTypeProvider().BASIC.BYTE){
				if(val<0||val>255){
					return new IntObject(val & 0x000000FF);
				}
			}

			return this;
		}
		case RuntimeType.FLOAT: return new FixedObject(val);
		case RuntimeType.STRING: return new StringObject(String.valueOf(val));
		case RuntimeType.TEXT: return new TextObject(String.valueOf(val));
		}
		return super.castTo(type);
	}
	
	@Override
	public IType getType(TypeProvider tp) {
		return tp.BASIC.INT;
	}
}
