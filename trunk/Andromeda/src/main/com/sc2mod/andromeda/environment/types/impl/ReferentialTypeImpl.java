package com.sc2mod.andromeda.environment.types.impl;

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.IReferentialType;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

public abstract class ReferentialTypeImpl extends RecordTypeImpl implements IReferentialType {
	
	protected HashSet<IInterface> interfaces = new HashSet<IInterface>(6);
	
	public HashSet<IInterface> getInterfaces() {
		return interfaces;
	}

	public ReferentialTypeImpl(GlobalStructureNode g, IScope s) {
		super(g, s);
	}
	

}
