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

import com.sc2mod.andromeda.environment.scopes.BlockScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.content.InheritableContentSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.vm.data.DataObject;

public abstract class TypeImpl extends BlockScope implements IType{

	private static int curCount = 1;
	private int hashCode;
	
	protected TypeImpl(IScope parentScope) {
		super(parentScope);
		hashCode = curCount++;
	}

	/**
	 * Types are always a static construct
	 */
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@Override
	public ScopedElementType getElementType() {
		return ScopedElementType.TYPE;
	}
	
	@Override
	public IScope getScope() {
		return getParentScope();
	}
		
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	
	
	/**
	 * Returns the description of this type's category. (Not for the type itself)
	 * @return description of the type category.
	 */
	@Override
	public abstract String getDescription();
	
	@Override
	public abstract TypeCategory getCategory();



	
	/**
	 * Returns iff a type can be cast implicitly to antoher type (i.e. without an explicit type cast).
	 * 
	 * The basic implementation for types returns true iff the
	 * type to be cast to is this type or if this type is a 
	 * subtype of the type.
	 */
	@Override
	public boolean canImplicitCastTo(IType toType){
		if(toType==this) 
			return true;
		if(isSubtypeOf(toType))
			return true;
		return false;
	}
	
	@Override
	public boolean canBeNull(){ return true;}
	
	@Override
	public abstract int getRuntimeType();
	
	@Override
	public String getGeneratedName(){ return getUid();}
	
	/**
	 * Gets the name used to define this type.
	 * For most types, this is just the generated name.
	 * However, for classes, this is not a pointer to a class.
	 * @return
	 */
	@Override
	public String getGeneratedDefinitionName(){ return getGeneratedName();}

	@Override
	public String getDefaultValueStr() { return "null"; }

	@Override
	public boolean canHaveFields() {
		return false;
	}
	
	/**
	 * States if this type is an implicit reference type, i.e., its values get
	 * passed by reference even if no pointer syntax is used.
	 * This is true for classes and interfaces.
	 * @return if this type is an implicit reference type.
	 */
	@Override
	public boolean isImplicitReferenceType(){
		return false;
	}
	
	/**
	 * Returns if this type can be used as parameter (structs and arrays cannot)
	 * @return
	 */
	@Override
	public boolean isValidAsParameter(){
		return true;
	}

	@Override
	public boolean canHaveMethods() {
		return false;
	}
	
	/**
	 * Returns the type that is generated for this type. For most types, it is the type
	 * itself. For classes, it is the top class of the hierarchy.
	 * @return
	 */
	@Override
	public IType getGeneratedType(){
		return this;
	}

	@Override
	public IType getWrappedType() {
		return null;
	}
	
	/**
	 * For extensions this method returns the type the extension is based of.
	 * This is not its direct ancestor but the top of the extension hierarchy, i.e. a basic type.
	 * For all other types this function is the identity.
	 * @return the base type for extensions
	 */
	@Override
	public IType getBaseType(){
		return this;
	}
	

	/**
	 * For extensions, this method returns the reachable base type.
	 * I.e. if this is a disjoint type, then the type itself is returned, otherwise the base type
	 * @return
	 */
	@Override
	public IType getReachableBaseType(){
		return this;
	}
	
	@Override
	public boolean isGenericInstance() {
		return false;
	}
	
	public boolean isGenericDecl(){
		return false;
	}
	
	@Override
	public String getFullName(){
		return getUid();
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

	
	@Override
	public boolean isTypeOrExtension(BasicType i) {
		return this == i;
	}
	
	/**
	 * Returns the "lowest" supertype of this type and t that is shared
	 * by both. Only working for extensions at the moment. For every
	 * other type, this is returned.
	 * @param t the other type
	 * @return
	 */
	@Override
	public IType getCommonSupertype(IType t){
		return this;
	}
	
	/**
	 * Returns the super type of this type or this type if it has no super type.
	 * @return super type or self
	 */
	@Override
	public IType getSuperType(){
		return this;
	}
	
	@Override
	public IType getNthAncestor(int n){
		IType result = this;
		for(;n>0;n--){
			result = result.getSuperType();
		}
		return result;
	}

	
	/**
	 * Returns true if this type is the given type or a subtype of it
	 * @return
	 */
	@Override
	public boolean isSubtypeOf(IType t){
		return this == t;
	}
	
	@Override
	public abstract int getByteSize();

	/**
	 * Returns the byte size that this type has when being a member of a struct
	 */	
	@Override
	public int getMemberByteSize() {
		return getByteSize();
	}
	
	/**
	 * Returns true iff this is an integer type (currently only int and byte).
	 * @return
	 */
	@Override
	public boolean isIntegerType(){
		return false;
	}
	
	/**
	 * Returns true iff this is a type extension declared with the iskey attribute.
	 * @return
	 */
	@Override
	public boolean isKeyType(){
		return false;		
	}

	@Override
	public DataObject getNextKey() {
		throw new Error("Keyof is not defined for the type " + this.getFullName());
	}
	
	//*** SCOPING METHODS ***
	
	@Override
	public IType getContainingType() {
		//TODO: Once types are allowed in other types, this needs to return the correct value
		return null;
	}
	
	@Override
	protected ScopeContentSet createContentSet() {
		return new InheritableContentSet(this);
	}
	
	@Override
	public void addInheritedContent(IScope parentScope) {
		((InheritableContentSet)getContent()).addInheritedContent(parentScope.getContent());
	}
	
	@Override
	public String getElementTypeName() {
		return "type";
	}
	

}
