/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types.basic;

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.NonReferentialType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class TypeInt extends NonReferentialType {

	public TypeInt() {
		super("int");
	}
	
	@Override
	public boolean canImplicitCastTo(IType toType) {
		return toType == this
			|| toType == FLOAT;
	}
	
	 
	@Override
	public boolean canConcatenateCastTo(IType toType) {
		if(toType == this|| toType == FLOAT || toType == STRING || toType == TEXT) return true;
		return false;
	}
	 
	@Override
	public String getDefaultValueStr() {
		return "0";
	}
	 
	@Override
	public int getRuntimeType() {
		 return RuntimeType.INT;
	}

	@Override
	public boolean isIntegerType(){
		return true;
	}
	
	@Override
	public IType getCommonSupertype(IType t) {
		IType base = t.getBaseType();
		if(base==BasicType.FLOAT) return t;
		return super.getCommonSupertype(t);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
