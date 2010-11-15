/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.access;

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.ImplicitFieldDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.FuncNameObject;

public class OpPointerNameAccess extends NameAccess{

	private IType pointerType;
	private ExprNode leftSide;
	public OpPointerNameAccess(ExprNode leftSide, TypeProvider typeProvider) {
		this.pointerType = leftSide.getInferedType();
		this.leftSide = leftSide;
	//	type = typeProvider.getSystemType(AndromedaSystemTypes.T_FUNC_NAME);
	}
	
//	@Override
//	public String getGeneratedName() {
//		throw new Error("Cannot yet get .name of a function pointer variable (only function constants are allowed at the moment)");
//	}
//	
//	@Override
//	public boolean isConst() {
//		return leftSide.isConstant();
//	}
//	
//	@Override
//	public DataObject getValue() {
//		VarDecl dataobject = (VarDecl) leftSide.getSemantics();
//		if(dataobject.getDeclType() == VarDecl.TYPE_FUNCTION_POINTER){
//			return new FuncNameObject(((OperationAccess)dataobject).getFunction());
//		}
//		return null;
//	}


	@Override
	public AccessType getAccessType() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public IScopedElement getAccessedElement() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public IScope getPrefixScope() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
