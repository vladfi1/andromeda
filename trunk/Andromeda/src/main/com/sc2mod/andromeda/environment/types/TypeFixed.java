/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types;

import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class TypeFixed extends NonReferentialType{

	public TypeFixed() {
		super("fixed");
	}
	
	 @Override
	public boolean canImplicitCastTo(IType toType) {
		 if(toType == this) return true;
		 return false;
	}
	 
	 @Override
	public boolean canExplicitCastTo(IType toType) {
		 return super.canExplicitCastTo(toType) || toType == INT || canConcatenateCastTo(toType);
	}
	 
	 @Override
	public boolean canConcatenateCastTo(IType toType) {
		if(toType == this || toType == STRING || toType == TEXT) return true;
		return false;
	}
	 
	 @Override
	public String getDefaultValueStr() {
		return "0.";
	}
	 
	 @Override
	public int getRuntimeType() {
		 return RuntimeType.FLOAT;
	}
	 


	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
