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
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;

/**
 * XPilot: modified to use Fixed type.
 */
public class FixedObject extends DataObject {
	
	private Fixed val;
	
	/*	
	public FixedObject(float val) {
		this.val = val;
	}
	*/
	
	public FixedObject(int val) {
		this.val = Fixed.fromInt(val);
	}
	
	public FixedObject(Number val) {
		this.val = Fixed.fromDecimal(val.doubleValue());
	}
	
	public FixedObject(Fixed val) {
		this.val = val;
	}
	
	/*
	@Override
	public float getFloatValue() {
		return (float)val.toDouble();
	}
	
	@Override
	public void setFloatValue(float f) {
		val = Fixed.fromDecimal(f);
	}
	*/
	
	@Override
	public Fixed getFixedValue() {
		return val;
	}
	
	@Override
	public void setFixedValue(Fixed f) {
		val = f;
	}
	
	@Override
	public String toString() {
//		if(val == (int)val){
//			return String.valueOf(val).concat(".0");
//		}
		//return String.format("%f", val);
		return val.toString();
	}
	
	@Override
	public ExprNode getExpression(TypeProvider tp) {
		return getLiteralExpr(tp, LiteralTypeSE.FLOAT);
	}
	
	@Override
	public int getIntValue() {
		return val.toInt();
	}
	
	@Override
	public IType getType(TypeProvider tp) {
		return tp.BASIC.FLOAT;
	}
	
	@Override
	public DataObject castTo(IType type) {
		switch(type.getRuntimeType()){
		case RuntimeType.FLOAT: return this;
		case RuntimeType.INT: return new IntObject(val.toInt());
		case RuntimeType.STRING: return new StringObject(String.valueOf(val));
		case RuntimeType.TEXT: return new TextObject(String.valueOf(val));
		}
		return super.castTo(type);	
	}

}
