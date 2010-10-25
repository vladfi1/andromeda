/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.operations;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.IAnnotatable;
import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.IGlobal;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.ScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;

import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;

public abstract class Operation extends SemanticsElement implements ScopedElement, IModifiable, IAnnotatable , SemanticsVisitorNode {

	private static int curHashCode = 1;
	private int hashCode = curHashCode++;
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public abstract void addImplicitLocals(ArrayList<LocalVarDecl> vars);
	
	public abstract void addInline();
	
	public abstract void addInvocation();
	
	public abstract void addReturnStmt(ReturnStmtNode r);

	public abstract boolean flowReachesEnd();
	
	/**
	 * Functions are not contained in any type
	 * @return null
	 */
	public abstract Type getContainingType();

	public abstract boolean isStrcall();
	
	public abstract String getDescription();
	
	public abstract String getGeneratedName();

	public abstract int getIndex();

	public abstract int getInvocationCount();
	
	public abstract LocalVarDecl[] getLocals();

	public abstract String getName();
	
	public abstract ParamDecl[] getParams();

	public abstract Type getReturnType();

	public abstract Signature getSignature();
	
	public String getNameAndSignature(){
		return new StringBuilder(64)
			.append(getName().toString())
			.append("(")
			.append(getSignature().toString())
			.append(")")
			.toString();
	}

	
	public abstract boolean hasBody();
	
	public abstract boolean isCreateCode();

	public abstract boolean isMarked();

	public abstract void mark();

	public abstract void setCreateCode(boolean createCode);

	
	public abstract void setFlowReachesEnd(boolean b);

	public abstract void setGeneratedName(String generatedName);

	public abstract void setIndex(int index);


	public abstract void setLocals(LocalVarDecl[] locals);

	public abstract void setName(String name);

	public abstract boolean usesThis();
	
	abstract void setReturnType(Type returnType);

	public abstract int getFunctionType();
	
	public abstract boolean isMember();
	
	public abstract StmtNode getBody();

	public abstract FuncPointerDecl getPointerDecl(TypeProvider tp);
	
	public void addOverride(Operation m) {
		throw new Error("Abstract method!");
	}

	public boolean isOverridden() {
		throw new Error("Abstract method!");
	}

	public boolean isCalledVirtually() {
		throw new Error("Abstract method!");
	}

	public Operation getOverridenMethod() {
		throw new Error("Abstract method!");
	}

	public int getVirtualTableIndex() {
		throw new Error("Abstract method!");
	}

	public int getCurVirtualCallChildIndex() {
		throw new Error("Abstract method!");
	}

	public void setVirtualCallIndex(int callIndex) {
		throw new Error("Abstract method!");
	}

	public void setVirtualTableIndex(int tableIndex) {
		throw new Error("Abstract method!");
	}

	public int getNextVirtualCallChildIndex() {
		throw new Error("Abstract method!");
	}

	public void setVirtualCallerName(String virtualCallerName) {
		throw new Error("Abstract method!");
	}

	public int getVirtualCallIndex() {
		throw new Error("Abstract method!");
	}

	public String getVirtualCaller() {
		throw new Error("Abstract method!");
	}

	protected void registerVirtualCall() {
		throw new Error("Abstract method!");
	}

	protected void setOverriddenMethod(Method method) {
		throw new Error("Abstract method!");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	@Override
	public ScopedElementType getElementType() {
		return ScopedElementType.OPERATION;
	}

	public abstract List<ImplicitParamDecl> getImplicitParams();
}