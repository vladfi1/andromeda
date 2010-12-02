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

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;

public class OperationAccess extends NameAccess{

	Operation func;
	int index = -1;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public OperationAccess(Operation func, TypeProvider typeProvider){
//		this.func = func;
//		type = typeProvider.registerFunctionPointerUsage(this);
	}
	
	public OperationAccess(Operation func) {
		this.func = func;
	}

//	@Override
//	public boolean isInitDecl() {
//		return false;
//	}
//
//	@Override
//	public IType getContainingType() {
//		return func.getContainingType();
//	}
//	
//	@Override
//	public SyntaxNode getDefinition() {
//		return func.getDefinition();
//	}
	
//	@Override
//	public String getGeneratedName() {
//		return String.valueOf(getIndex());
//	}
	
	/**
	 * Functions are always constant
	 */
	public boolean isConst() {
		return true;
	}
	
	@Override
	public boolean isStatic() {
		return func.isStaticElement();
	}
	
//	@Override
//	public DataObject getValue() {
//		return new FunctionObject(this);
//	}


	@Override
	public AccessType getAccessType() {
		return AccessType.OP_POINTER;
	}

	@Override
	public Operation getAccessedElement() {
		return func;
	}

	@Override
	public IScope getPrefixScope() {
		throw new Error("Not implemented!");
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
