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
	private Operation overrides;
	private ArrayList<Operation> overriders;
	private int overrideCount;
	private boolean isCalledVirtually;
	private int virtualCallIndex;
	private int virtualTableIndex;
	private int virtualCallOffset;
	private String virtualCaller;
	
	public Method( MethodDeclNode functionDeclaration, Type containingType, Scope scope) {
		super(functionDeclaration,scope);
		this.containingType = containingType;
	}
	
	//XPilot: for method proxies
	protected Method() {}
	

	@Override
	public String getVirtualCaller() {
		return virtualCaller;
	}

	@Override
	public void setVirtualCallerName(String virtualCallerName) {
		virtualCaller = virtualCallerName;
	}
	
	@Override
	public int getNextVirtualCallChildIndex() {
		virtualCallOffset++;
		return virtualCallIndex + virtualCallOffset;
	}
	
	@Override
	public int getCurVirtualCallChildIndex() {
		return virtualCallIndex + virtualCallOffset;
	}
	
	@Override
	public int getVirtualTableIndex() {
		return virtualTableIndex;
	}

	@Override
	public void setVirtualTableIndex(int virtualTableIndex) {
		this.virtualTableIndex = virtualTableIndex;
	}

	@Override
	public int getVirtualCallIndex() {
		return virtualCallIndex;
	}

	@Override
	public void setVirtualCallIndex(int virtualCallIndex) {
		this.virtualCallIndex = virtualCallIndex;
	}
	
	@Override
	protected void setOverriddenMethod(Method method) {
		overrides = method;
	}

	@Override
	public void addOverride(Operation overrider) {
//		System.out.println(overrider.getDescription() + " -> " + this.getDescription());
		overrider.setOverriddenMethod(this);
		overrideCount++;
		if(overriders==null) overriders = new ArrayList<Operation>(2);
		overriders.add(overrider);
	}
	
	@Override
	public String getDescription() {
		Type t = getContainingType();
		if(t == null) return "method " + getUid();
		return "method " + t.getFullName() + "." + getUid();
	}
	
	@Override
	public boolean isOverridden() {
		return overriders != null;
	}
	
	public ArrayList<Operation> getOverridingMethods() {
		return overriders;		
	}
	
	@Override
	public Operation getOverridenMethod() {
		return overrides;
	}
	
	/**
	 * Only called by VirtualCallResolver.
	 */
	@Override
	public void registerVirtualCall() {

		//Already called virtually, skip!
		//if(isCalledVirtually) return;

		//This and all overriders are called virtually
		isCalledVirtually = true;
		
		// XPilot: these will be set in VirtualCallResolver
		/*
		if(overriders != null)
			for(AbstractFunction m: overriders) {
				m.registerVirtualCall();
			}
		*/
	}
	
	@Override
	public boolean isCalledVirtually() {
		return isCalledVirtually;
	}
	
	@Override
	public int getFunctionType() {
		return isStatic?TYPE_STATIC_METHOD:TYPE_METHOD;
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
	
	@Override public boolean usesThis() { return !isStatic; }
	@Override public boolean isMember() { return !isStatic; }

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
