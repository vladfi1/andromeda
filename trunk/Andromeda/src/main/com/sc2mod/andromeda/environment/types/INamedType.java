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

import com.sc2mod.andromeda.environment.Signature;
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
	INamedType createGenericInstance(Signature s);
	
	
	/**
	 * Gets a generic instance of this type. Tries to get a cached type before creating a new one.
	 * @param s the signature of the type values to replace the parameters.
	 * @return
	 */
	INamedType getGenericInstance(Signature s);
	
	/**
	 * Sets the type parameters for this type, hence making it generic.
	 * This is called by the semantic analysis after the types of the type parameters can be assigned.
	 * @param types
	 */
	void setTypeParameters(TypeParameter[] types);
	
	/**
	 * Returns true iff this is a generic type.
	 * Generic type means, that this is a type
	 * which has type parameters in its declaration.
	 * Also a concrete instance is considered to be generic.
	 * <br/>
	 * What is not considered generic is a type that is extended from a
	 * generic type but replaces all parameters by concrete types.
	 * Examples:
	 * <p><code>
	 * List&lt;T&gt; // true<br/>
	 * List&lt;Integer&gt; // true<br/>
	 * class X extends ListList&lt;Integer&gt; // false
	 * </code></p>
	 * 
	 * 
	 **/
	boolean isGeneric();
	
	/**
	 * Returns true iff this is a generic type without concrete parameters.
	 * Examples:
	 * <p><code>
	 * List&lt;T&gt; // true<br/>
	 * List&lt;Integer&gt; // false<br/>
	 * class X extends ListList&lt;Integer&gt; // false
	 * </code></p>
	 * 
	 * @return
	 */
	boolean isGenericDecl();
	
	

}
