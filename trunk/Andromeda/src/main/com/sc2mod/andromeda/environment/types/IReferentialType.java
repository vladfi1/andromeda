package com.sc2mod.andromeda.environment.types;

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

public interface IReferentialType extends IRecordType {
	
	HashSet<IInterface> getInterfaces();


}
