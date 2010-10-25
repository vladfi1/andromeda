/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.ArrayList;

import com.sc2mod.andromeda.environment.types.Interface;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;

public class Method extends Function {

	private boolean isAbstract;
	private boolean isStatic;
	private RecordType containingType;
	private AbstractFunction overrides;
	private ArrayList<AbstractFunction> overriders;
	private int overrideCount;
	private boolean isCalledVirtually;
	private int virtualCallIndex;
	private int virtualTableIndex;
	private int virtualCallOffset;
	private String virtualCaller;
	

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
	public void addOverride(AbstractFunction overrider) {
//		System.out.println(overrider.getDescription() + " -> " + this.getDescription());
		overrider.setOverriddenMethod(this);
		overrideCount++;
		if(overriders==null) overriders = new ArrayList<AbstractFunction>(2);
		overriders.add(overrider);
	}
	
	@Override
	public String getDescription() {
		RecordType t = getContainingType();
		if(t == null) return "method " + getUid();
		return "method " + t.getFullName() + "." + getUid();
	}
	
	@Override
	public boolean isOverridden() {
		return overriders != null;
	}
	
	public ArrayList<AbstractFunction> getOverridingMethods() {
		return overriders;		
	}
	
	@Override
	public AbstractFunction getOverridenMethod() {
		return overrides;
	}
	
	/**
	 * Only called by VirtualCallResolver.
	 */
	@Override
	public void registerVirtualCall() {
//		System.out.println("Registering virtual call for " + this);
		
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
	public RecordType getContainingType() {
		return containingType;
	}
	
	public Method(MethodDeclNode functionDeclaration, RecordType containingType, Scope scope) {
		super(functionDeclaration,scope);
		this.containingType = containingType;
		if(body==null) {
			if(containingType instanceof Interface) {
				if(isAbstract) {
					throw Problem.ofType(ProblemId.ABSTRACT_INTERFACE_METHOD).at(declaration)
								.raiseUnrecoverable();
				}
			} else
			if(!isAbstract) {
				throw Problem.ofType(ProblemId.NON_ABSTRACT_METHOD_WITHOUT_BODY).at(declaration)
								.raiseUnrecoverable();
			}
		} else {
			if(isAbstract) {
				throw Problem.ofType(ProblemId.ABSTRACT_METHOD_WITH_BODY).at(declaration)
								.raiseUnrecoverable();
			}
		}
		if(isStatic) {
			if(isFinal()) 
				throw Problem.ofType(ProblemId.STATIC_METHOD_FINAL).at(declaration.getHeader().getModifiers())
						.raiseUnrecoverable();
			if(isAbstract)
				throw Problem.ofType(ProblemId.STATIC_METHOD_ABSTRACT).at(declaration.getHeader().getModifiers())
						.raiseUnrecoverable();
			if(body==null)
				throw Problem.ofType(ProblemId.STATIC_METHOD_WITHOUT_BODY).at(declaration.getHeader())
						.raiseUnrecoverable();
		}
	}
	
	//XPilot: for method proxies
	protected Method() {}
	
	@Override
	public void setNative() {
		throw Problem.ofType(ProblemId.METHOD_DECLARED_NATIVE).at(declaration.getHeader().getModifiers())
					.raiseUnrecoverable();
	}

	@Override
	public void setAbstract() {
		isAbstract = true;
	}
	
	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
	
	@Override
	public boolean isStatic() {
		return isStatic;
	}
	
	@Override
	public void setStatic() {
		isStatic = true;
	}

	@Override
	public boolean usesThis() {
		return !isStatic;
	}

	@Override
	public boolean isMember() {
		return !isStatic;
	}
}
