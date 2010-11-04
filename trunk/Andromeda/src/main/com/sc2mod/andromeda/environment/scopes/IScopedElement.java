package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.types.IType;

public interface IScopedElement extends IIdentifiable, IDefined {

	public abstract Visibility getVisibility();
	public abstract IScope getScope();
	public abstract IType getContainingType();
	
	public abstract boolean isStatic();
	
	/**
	 * Which kind of type this scoped element is (operation, variable, type, error)
	 * @return
	 */
	public abstract ScopedElementType getElementType();
	
	/**
	 * Returns a string representation which kind of element this is.
	 * Examples: "function" "constructor" "variable"
	 * 
	 * Used for building error messages.
	 * @return the type name of this element.
	 */
	public abstract String getElementTypeName();

}
