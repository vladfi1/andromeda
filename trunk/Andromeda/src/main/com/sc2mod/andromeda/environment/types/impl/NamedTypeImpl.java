/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types.impl;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.INamedType;
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
public abstract class NamedTypeImpl extends TypeImpl implements INamedType{
	
	protected String generatedName;
	private TypeParameter[] typeParams;
	private final String name;
	
	protected NamedTypeImpl(IScope parentScope, String name){
		super(parentScope);
		this.name = name;
	}
	
	/**
	 * Constructor for generic instances of a type.
	 * @param gen use always the constant GENERIC.
	 * @param genericParent the type for which to create a generic instance.
	 */
	protected NamedTypeImpl(INamedType genericParent, Signature s){
		this(genericParent.getScope(), genericParent.getName());
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public String getUid() {
		return getName();
	}
	
	@Override
	public String getFullName() {
		return getName();
	}
	
	@Override
	public String getGeneratedName() {
		return generatedName==null?getName():generatedName;
	}

	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}
	
	/**
	 * Sets the type parameters for this type, hence making it generic.
	 * This is called by the semantic analysis after the types of the type parameters can be assigned.
	 * @param types
	 */
	public void setTypeParameters(TypeParameter[] types){
		typeParams = types;
	}
	
	@Override
	public TypeParameter[] getTypeParameters() {
		return typeParams;
	}

	@Override
	public boolean isGenericDecl(){
		return typeParams != null;
	}

	@Override
	public Signature getTypeArguments() {
		return null;
	}
	

}
