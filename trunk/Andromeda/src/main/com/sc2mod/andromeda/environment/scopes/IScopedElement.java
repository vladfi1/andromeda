package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.types.IType;

public interface IScopedElement extends IIdentifiable, IDefined {

	public abstract Visibility getVisibility();
	public abstract IScope getScope();
	public abstract IType getContainingType();
	
	/**
	 * Returns true, iff this is a static element.
	 * A static element is an element that belongs to no specific instance of any type
	 * but is rather a global construct.
	 * 
	 * Global functions and variables are always static elements. Class members are
	 * static elements if they are defined to be static.
	 * @return
	 */
	public abstract boolean isStaticElement();
	
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
