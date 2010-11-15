/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.annotations.IAnnotatable;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.notifications.InternalProgramError;


/**
 * A named type is a type which has an own name.
 * This can be basic types like int, classes, interfaces, extensions,...
 * 
 * Types that have no own name and hence do not belong into this category:
 * function pointers, array types, pointers,...
 * 
 * Named types (except basic and special types) can all be generic.
 * This is why the generics mechanism is handled by this class.
 * @author gex
 *
 */
public interface INamedType extends IType{
	
	String getName();

	void setGeneratedName(String generatedName);
	
	/**
	 * Factory method that creates a generic type instance from a type.
	 * 
	 * Should be implemented by each concrete subclass to construct a generic
	 * instance which delegates most calls to the type itself, despite some
	 * generic related calls.
	 * @return the created instance
	 */
	//TODO: Factor into visitor
	@Deprecated
	INamedType createGenericInstance(Signature s);

	
	/**
	 * Sets the type parameters for this type, hence making it a generic declaration.
	 * This is called by the semantic analysis after the types of the type parameters can be assigned.
	 * @param types
	 */
	void setTypeParameters(TypeParameter[] types);
	
	TypeParameter[] getTypeParameters();
	
	
	/**
	 * Returns the type arguments of this type or null if this
	 * is no generic type or a generic declaration (only generic instances have type arguments)
	 * @return
	 */
	Signature getTypeArguments();
	
	

	
	

}
