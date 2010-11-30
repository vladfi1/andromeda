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
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;

public class TypeByte extends NonReferentialType {

	public TypeByte(TypeProvider t) {
		super("byte",t);
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
	public int getByteSize() {
		return 0;
	}
	
	public boolean isIntegerType(){
		return true;
	}
	
	@Override
	public boolean canImplicitCastTo(IType toType) {
		if(toType.getBaseType().isIntegerType()) return true;
		return super.canImplicitCastTo(toType);
	}
	
	@Override
	public IType getCommonSupertype(IType t) {
		IType base = t.getBaseType();
		if(base==tprov.BASIC.INT||base==tprov.BASIC.FLOAT) return t;
		return super.getCommonSupertype(t);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
