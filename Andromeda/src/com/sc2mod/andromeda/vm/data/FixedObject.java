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

import java.math.RoundingMode;
import java.text.NumberFormat;

import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;

public class FixedObject extends DataObject {
	
	private float val;
	
		
	public FixedObject(float val) {
		this.val = val;
	}
	
	public FixedObject(Number val){
		this.val = val.floatValue();
	}

	@Override
	public float getFloatValue() {
		return val;
	}
	
	@Override
	public void setFloatValue(float f) {
		val = f;
	}
	
	@Override
	public String toString() {
//		if(val == (int)val){
//			return String.valueOf(val).concat(".0");
//		}
		//Formatter f = new Formatter();
		//f.format("%f", val);
		//System.out.println(val + ": " + f.toString());
		return String.format("%f", val);
	}
	
	
	
	@Override
	public Expression getExpression() {
		return getLiteralExpr(LiteralType.FLOAT);
	}
	
	@Override
	public int getIntValue() {
		return (int)val;
	}
	
	@Override
	public Type getType() {
		return BasicType.FLOAT;
	}
	
	@Override
	public DataObject castTo(Type type) {
		switch(type.getRuntimeType()){
		case RuntimeType.FLOAT: return this;
		case RuntimeType.INT: return new IntObject((int)val);
		case RuntimeType.STRING: return new StringObject(String.valueOf(val));
		case RuntimeType.TEXT: return new TextObject(String.valueOf(val));
		}
		return super.castTo(type);	
	}
	

}
