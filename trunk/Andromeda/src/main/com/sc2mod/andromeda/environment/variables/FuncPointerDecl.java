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

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.FunctionObject;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.types.FunctionPointer;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class FuncPointerDecl extends VarDecl{

	Operation func;
	int index = -1;
	
	@Override
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public FuncPointerDecl(Operation func, TypeProvider typeProvider){
		this.func = func;
		type = typeProvider.registerFunctionPointerUsage(this);
	}
	
	@Override
	public int getDeclType() {
		return TYPE_FUNCTION_POINTER;
	}

	@Override
	public boolean isInitDecl() {
		return false;
	}

	@Override
	public IType getContainingType() {
		return func.getContainingType();
	}
	
	@Override
	public SyntaxNode getDefinition() {
		return func.getDefinition();
	}

	public Operation getFunction() {
		return func;
	}
	
	@Override
	public String getGeneratedName() {
		return String.valueOf(getIndex());
	}
	
	/**
	 * Functions are always constant
	 */
	@Override
	public boolean isConst() {
		return true;
	}
	
	@Override
	public boolean isStatic() {
		return func.isStatic();
	}
	
	@Override
	public DataObject getValue() {
		return new FunctionObject(this);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
