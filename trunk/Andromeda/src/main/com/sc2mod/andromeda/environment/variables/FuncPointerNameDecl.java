/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.FuncNameObject;

public class FuncPointerNameDecl extends ImplicitFieldDecl{

	private IType pointerType;
	private ExprNode leftSide;
	public FuncPointerNameDecl(ExprNode leftSide, TypeProvider typeProvider) {
		this.pointerType = leftSide.getInferedType();
		this.leftSide = leftSide;
		type = typeProvider.getSystemType(AndromedaSystemTypes.T_FUNC_NAME);
	}
	
	@Override
	public String getGeneratedName() {
		throw new Error("Cannot yet get .name of a function pointer variable (only function constants are allowed at the moment)");
	}
	
	@Override
	public boolean isConst() {
		return leftSide.isConstant();
	}
	
	@Override
	public DataObject getValue() {
		VarDecl dataobject = (VarDecl) leftSide.getSemantics();
		if(dataobject.getDeclType() == VarDecl.TYPE_FUNCTION_POINTER){
			return new FuncNameObject(((FuncPointerDecl)dataobject).getFunction());
		}
		return null;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
