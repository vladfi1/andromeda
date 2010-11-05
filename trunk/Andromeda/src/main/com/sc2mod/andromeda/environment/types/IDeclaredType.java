package com.sc2mod.andromeda.environment.types;

import com.sc2mod.andromeda.environment.IAnnotatable;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

public interface IDeclaredType extends INamedType,IModifiable, IAnnotatable {

	@Override
	abstract GlobalStructureNode getDefinition();
	
}
