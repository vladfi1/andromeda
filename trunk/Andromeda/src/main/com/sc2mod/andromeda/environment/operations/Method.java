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

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.semAnalysis.ResolveAndCheckTypesVisitor;
import com.sc2mod.andromeda.semAnalysis.SemanticsCheckerAndResolver;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.types.Interface;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class Method extends Function {

	private boolean isAbstract;
	private boolean isStatic;
	private Type containingType;
	private OverrideInformation overrideInformation;
	
	public Method( MethodDeclNode functionDeclaration, Type containingType, Scope scope) {
		super(functionDeclaration,scope);
		this.containingType = containingType;
		if(!isStatic){
			overrideInformation = new OverrideInformation(this);
		}
	}
	
	//XPilot: for method proxies
	protected Method() {}
	
	@Override
	public OverrideInformation getOverrideInformation() {
		return overrideInformation;
	}

	
	
	@Override
	public String getDescription() {
		Type t = getContainingType();
		if(t == null) return "method " + getUid();
		return "method " + t.getFullName() + "." + getUid();
	}
	


	
	@Override
	public OperationType getOperationType() {
		return isStatic?OperationType.STATIC_METHOD:OperationType.METHOD;
	}
	
	@Override
	public Type getContainingType() {
		return containingType;
	}
	
	@Override
	public String getElementTypeName() {
		return "method";
	}

	
	@Override
	public void setNative() {
		throw Problem.ofType(ProblemId.METHOD_DECLARED_NATIVE).at(declaration.getHeader().getModifiers())
					.raiseUnrecoverable();
	}

	@Override public void setAbstract() {	isAbstract = true; }
	@Override public boolean isAbstract() { return isAbstract; }
	
	@Override public void setStatic() { isStatic = true; }
	@Override public boolean isStatic() { return isStatic; }

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
