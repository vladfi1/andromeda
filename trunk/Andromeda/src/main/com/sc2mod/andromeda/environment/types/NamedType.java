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
import com.sc2mod.andromeda.environment.scopes.Scope;
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
public abstract class NamedType extends Type{
	
	protected HashMap<Signature,NamedType> genericInstances;
	private Signature typeParamSignature;
	protected String generatedName;
	
	public abstract String getName();
	
	@Override
	public String getGeneratedName() {
		return generatedName==null?getName():generatedName;
	}

	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}
	
	/**
	 * Factory method that creates a generic type instance from a type.
	 * 
	 * Should be implemented by each concrete subclass to construct a generic
	 * instance which delegates most calls to the type itself, despite some
	 * generic related calls.
	 * @return the created instance
	 */
	protected abstract NamedType createGenericInstance(Signature s);
	
	
	/**
	 * Gets a generic instance of this type. Tries to get a cached type before creating a new one.
	 * @param s the signature of the type values to replace the parameters.
	 * @return
	 */
	public NamedType getGenericInstance(Signature s){
		if(!isGeneric()){
			throw new InternalProgramError("Trying to create a generic instance of the non generic type " + this.getName());
		}
		
		if(s.size() != typeParamSignature.size()){
			throw new InternalProgramError("Trying to create a generic instance with the wrong number of type values");
		}
		
		//No new type, just the type itself.
		if(s.equals(typeParamSignature)) return this;
		
		//Lazily instanciate generic instance map
		if(genericInstances == null){
			genericInstances = new HashMap<Signature, NamedType>();
		}
		
		//Get the generic instance from the map. If we haven't got it yet, then create and put into map.
		NamedType g = genericInstances.get(s);
		if(g == null){
			g = createGenericInstance(s);
			genericInstances.put(s, g);
		}
		return g;
	}
	
	/**
	 * Sets the type parameters for this type, hence making it generic.
	 * This is called by the semantic analysis after the types of the type parameters can be assigned.
	 * @param types
	 */
	public void setTypeParameters(TypeParameter[] types){
		typeParamSignature = new Signature(types);
	}
	
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
	public boolean isGeneric(){
		//TODO Check if this reflects the documentation
		return typeParamSignature == null;			
	}
	
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
	public boolean isGenericDecl(){
		//TODO Check if this reflects the documentation
		return typeParamSignature == null;
	}
	

	
	
	/**
	 * This identifier is used because otherwise, the constructors
	 * would be ambiguous since a named type is also a scope.
	 */
	protected static GenericIdentifier GENERIC = new GenericIdentifier();
	protected static class GenericIdentifier{
		private GenericIdentifier(){}
	}
	
	protected NamedType(Scope parentScope){
		super(parentScope);
		initMembers();
	}
	
	/**
	 * Constructor for generic instances of a type.
	 * @param gen use always the constant GENERIC.
	 * @param genericParent the type for which to create a generic instance.
	 */
	protected NamedType(GenericIdentifier gen, NamedType genericParent){
		super(genericParent.getScope());
	}
	
	/**
	 * Method that is called in the constructor
	 * if a normal (not a generic type instance) type is created.
	 * 
	 * 
	 * Here, all members should be initialized.
	 * Do not initialize members in the fields themselves, as this will
	 * be useless overhead for generic instances, which do not use these
	 * members.
	 * The only exception is if a member is also needed by the generic instance
	 * and not delegated.
	 * 
	 * The method should be overridden by subtypes that want to init members.
	 * The overriding definition MUST always call super.initMembers() before doing any init
	 * to ensure that the superclass fields are inited correctly.
	 */
	protected void initMembers() {}
	

}
