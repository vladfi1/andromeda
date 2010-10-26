package com.sc2mod.andromeda.environment.operations;

import java.util.ArrayList;

public class OverrideInformation {

	private String virtualCaller;
	private int virtualCallOffset;
	private int virtualCallIndex;
	private int virtualTableIndex;
	private Method overrides;
	private boolean isCalledVirtually;
	private ArrayList<Operation> overriders;
	private Method meth;
	private int overrideCount;
	
	public OverrideInformation(Method meth) {
		this.meth = meth;
	}
	
	public String getVirtualCaller() {
		return virtualCaller;
	}

	public void setVirtualCallerName(String virtualCallerName) {
		virtualCaller = virtualCallerName;
	}
	
	public int getNextVirtualCallChildIndex() {
		virtualCallOffset++;
		return virtualCallIndex + virtualCallOffset;
	}
	
	public int getCurVirtualCallChildIndex() {
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
	
	protected void setOverriddenMethod(Method method) {
		overrides = method;
	}

	public void addOverride(Operation overrider) {
//		System.out.println(overrider.getDescription() + " -> " + this.getDescription());
		overrider.getOverrideInformation().setOverriddenMethod(meth);
		overrideCount++;
		if(overriders==null) overriders = new ArrayList<Operation>(2);
		overriders.add(overrider);
	}
	
	public boolean isOverridden() {
		return overriders != null;
	}
	
	public ArrayList<Operation> getOverridingMethods() {
		return overriders;		
	}
	
	public Operation getOverridenMethod() {
		return overrides;
	}
	
	/**
	 * Only called by VirtualCallResolver.
	 */
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
	
	
	public boolean isCalledVirtually() {
		return isCalledVirtually;
	}
}
