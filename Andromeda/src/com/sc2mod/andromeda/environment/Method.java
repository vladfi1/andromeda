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
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;

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
	

	public String getVirtualCaller() {
		return virtualCaller;
	}

	public void setVirtualCallerName(String virtualCallerName) {
		virtualCaller = virtualCallerName;
	}
	
	public int getNextVirtualCallChildIndex(){
		virtualCallOffset++;
		return virtualCallIndex + virtualCallOffset;
	}
	
	public int getCurVirtualCallChildIndex(){
		return virtualCallIndex + virtualCallOffset;
	}
	
	public int getVirtualTableIndex() {
		return virtualTableIndex;
	}

	public void setVirtualTableIndex(int virtualTableIndex) {
		this.virtualTableIndex = virtualTableIndex;
	}

	public int getVirtualCallIndex() {
		return virtualCallIndex;
	}

	public void setVirtualCallIndex(int virtualCallIndex) {
		this.virtualCallIndex = virtualCallIndex;
	}
	
	@Override
	protected void setOverriddenMethod(Method method) {
		overrides = method;
	}

	@Override
	public void addOverride(AbstractFunction overrider){
		overrider.setOverriddenMethod(this);
		overrideCount++;
		if(overriders==null) overriders = new ArrayList<AbstractFunction>(2);
		overriders.add(overrider);
	}
	
		
	public String getDescription(){
		RecordType t = getContainingType();
		if(t == null) return "method " + getUid();
		return "method " + getContainingType().getUid() + "." + getUid();
	}
	
	public boolean isOverridden(){
		return overriders != null;
	}
	
	public ArrayList<AbstractFunction> getOverridingMethods(){
		return overriders;		
	}
	
	public AbstractFunction getOverridenMethod(){
		return overrides;
	}
	
	public void registerVirtualCall(){
		//Already called virtually, skip!
		if(isCalledVirtually) return;

		//This and all overriders are called virtually
		isCalledVirtually = true;
		if(overriders != null)
			for(AbstractFunction m: overriders){
				m.registerVirtualCall();
			}
	}
	
	public boolean isCalledVirtually(){
		return isCalledVirtually;
	}
	
	public int getFunctionType(){
		return isStatic?TYPE_STATIC_METHOD:TYPE_METHOD;
	}
	
	public RecordType getContainingType() {
		return containingType;
	}
	
	public Method(MethodDeclaration functionDeclaration, RecordType containingType, Scope scope) {
		super(functionDeclaration,scope);
		this.containingType = containingType;
		if(body==null){
			if(containingType instanceof Interface){
				if(isAbstract){
					throw new CompilationError(declaration, "Inteface methods may not be declared abstract (they are implicitly).");
				}
			} else
			if(!isAbstract){
				throw new CompilationError(declaration, "A method was not declared abstract must have a body.");
			}
		} else {
			if(isAbstract){
				throw new CompilationError(declaration, "A method was declared abstract may not have a body.");
			}
		}
		if(isStatic){
			if(isFinal()) throw new CompilationError(declaration.getHeader().getModifiers(), "Static methods cannot be final.");
			if(isAbstract)throw new CompilationError(declaration.getHeader().getModifiers(), "Static methods cannot be abstract.");
			if(body==null)throw new CompilationError(declaration.getHeader().getModifiers(), "Static methods cannot be declared without body.");
		}
	}
	
	@Override
	public void setNative() {
		throw new CompilationError(declaration.getHeader().getModifiers(),"Methods cannot be declared 'native'");
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
