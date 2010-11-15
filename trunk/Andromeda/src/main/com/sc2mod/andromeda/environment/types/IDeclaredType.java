package com.sc2mod.andromeda.environment.types;

import java.util.LinkedList;

import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.annotations.IAnnotatable;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

public interface IDeclaredType extends INamedType,IModifiable, IAnnotatable {

	@Override
	abstract GlobalStructureNode getDefinition();
	
	LinkedList<IDeclaredType> getDescendants();
	
	 /**
	  *	Returns true if this type is the top of the inheritance hierarchy.
	  * For classes, this is true if they extend no other class.
	  * For extensions, this is true if they directly extend a native type.
	  * @return
	  */
	 public boolean isTopType();
	
}
