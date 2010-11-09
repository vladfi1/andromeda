package com.sc2mod.andromeda.environment.access;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;

public abstract class NameAccess implements SemanticsElement{
	
	

	public abstract AccessType getAccessType();
	
	public abstract boolean isStatic();
	
	public abstract IScopedElement getAccessedElement();
	
	public abstract IScope getPrefixScope();
	
}
