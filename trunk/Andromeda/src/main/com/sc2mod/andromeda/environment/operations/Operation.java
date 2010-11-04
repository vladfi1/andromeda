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
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;

import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;

public abstract class Operation extends SemanticsElement implements IScopedElement, IModifiable, IAnnotatable , SemanticsVisitorNode {

	private static int curHashCode = 1;
	private int hashCode = curHashCode++;
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public abstract void addImplicitLocals(ArrayList<LocalVarDecl> vars);
	
	public abstract void addReturnStmt(ReturnStmtNode r);

	public abstract boolean flowReachesEnd();
	
	/**
	 * Functions are not contained in any type
	 * @return null
	 */
	public abstract IType getContainingType();

	public abstract boolean isStrcall();
	
	public abstract String getDescription();
	
	
	public abstract LocalVarDecl[] getLocals();

	public abstract String getName();
	
	public abstract ParamDecl[] getParams();

	public abstract IType getReturnType();

	public abstract Signature getSignature();
	
	public abstract boolean hasBody();
	
	public abstract String getGeneratedName();
	public abstract void setGeneratedName(String generatedName);
	
	public abstract int getInvocationCount();
	
	public abstract void addInline();
	
	public abstract void addInvocation();
	
	public abstract boolean isCreateCode();
	public abstract void setCreateCode(boolean createCode);
	
	public abstract void setFlowReachesEnd(boolean b);



	public abstract void setLocals(LocalVarDecl[] locals);

	public abstract void setName(String name);
	
	abstract void setReturnType(IType returnType);

	public abstract OperationType getOperationType();
	
	public abstract FuncPointerDecl getPointerDecl(TypeProvider tp);
	
	public OverrideInformation getOverrideInformation(){
		throw new Error("Cannot get override information for a non method operation!");
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
