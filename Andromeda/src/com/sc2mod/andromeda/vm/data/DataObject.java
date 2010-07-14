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

import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;

public abstract class DataObject {
	
	public abstract Type getType();

	public void setBoolValue(boolean b){
		throw new ExecutionError("Cannot set to this type!");
	}
	
	public boolean getBoolValue(){
		throw new ExecutionError("Cannot get this type!");
	}
	
	public void setIntValue(int i){
		throw new ExecutionError("Cannot set to this type!");
	}
	
	public int getIntValue(){
		throw new ExecutionError("Cannot get this type!");
	}
	
	public void setFloatValue(float f){
		throw new ExecutionError("Cannot set to this type!");
	}
	
	public float getFloatValue(){
		throw new ExecutionError("Cannot get this type!");
	}
	
	public void setStringValue(String s){
		throw new ExecutionError("Cannot set to this type!");
	}
	
	public String getStringValue(){
		throw new ExecutionError("Cannot get this type!");
	}

	public boolean isNull() {
		throw new ExecutionError("Cannot get this type!");
	}
	
	public abstract Expression getExpression();
	
	protected LiteralExpression getLiteralExpr(int lt){
		LiteralExpression le = new LiteralExpression(new Literal(this, lt));
		le.setInferedType(getType());
		return le;
	}

	public DataObject castTo(Type type) {
		if(type == getType()) return this;
		throw new ExecutionError("Cannot cast from " + getType().getFullName() + " to " + type.getFullName());
	}

	public boolean doNotInline() {
		return false;
	}
}
