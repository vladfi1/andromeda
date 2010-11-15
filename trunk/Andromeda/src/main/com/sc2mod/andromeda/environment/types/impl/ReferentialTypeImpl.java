package com.sc2mod.andromeda.environment.types.impl;

import java.util.HashSet;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.IReferentialType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

public abstract class ReferentialTypeImpl extends RecordTypeImpl implements IReferentialType {
	
	protected HashSet<IInterface> interfaces = new HashSet<IInterface>(6);
	private int typeIndex;

	public HashSet<IInterface> getInterfaces() {
		return interfaces;
	}

	public ReferentialTypeImpl(GlobalStructureNode g, IScope s, Environment env) {
		super(g, s, env);
	}
	
	@Override
	public int getRuntimeType() {
		return RuntimeType.INT;
	}
	
	public int getTypeIndex() {
		return typeIndex;
	}

	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}	
	
	
	

}
