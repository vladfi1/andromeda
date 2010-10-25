package com.sc2mod.andromeda.environment.types;

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

public abstract class ReferentialType extends RecordType {
	
	protected HashSet<Interface> interfaces = new HashSet<Interface>(6);
	
	public HashSet<Interface> getInterfaces() {
		return interfaces;
	}

	public ReferentialType(GlobalStructureNode g, Scope s) {
		super(g, s);
	}

}
