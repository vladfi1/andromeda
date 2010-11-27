package com.sc2mod.andromeda.environment.operations;

import java.util.ArrayList;

import com.sc2mod.andromeda.problems.InternalProgramError;

public class OverrideInformation {

	private Operation overrides;
	protected Operation op;
	
	public OverrideInformation(Operation op) {
		this.op = op;
	}
	
	public String getVirtualCaller() {
		throw new InternalProgramError("!");
	}

	public void setVirtualCallerName(String virtualCallerName) {
		throw new InternalProgramError("!");
	}
	
	public int getNextVirtualCallChildIndex() {
		throw new InternalProgramError("!");
	}
	
	public int getCurVirtualCallChildIndex() {
		throw new InternalProgramError("!");
	}
	
	public int getVirtualTableIndex() {
		throw new InternalProgramError("!");
	}

	public void setVirtualTableIndex(int virtualTableIndex) {
		throw new InternalProgramError("!");
	}

	public int getVirtualCallIndex() {
		throw new InternalProgramError("!");
	}

	
	public void setVirtualCallIndex(int virtualCallIndex) {
		throw new InternalProgramError("!");
	}
	
	protected void setOverriddenMethod(Operation method) {
		overrides = method;
	}

	public void addOverride(Operation overrider) {
		overrider.getOverrideInformation().setOverriddenMethod(op);
	}
	
	public boolean isOverridden() {
		throw new InternalProgramError("!");
	}
	
	public ArrayList<Operation> getOverridingMethods() {
		throw new InternalProgramError("!");	
	}
	
	public Operation getOverridenMethod() {
		return overrides;
	}
	
	/**
	 * Only called by VirtualCallResolver.
	 */
	public void registerVirtualCall() {
		throw new InternalProgramError("!");
	}
	
	
	public boolean isCalledVirtually() {
		throw new InternalProgramError("!");
	}
}
