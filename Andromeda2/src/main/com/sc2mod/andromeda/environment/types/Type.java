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

import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public abstract class Type extends SemanticsElement implements IIdentifiable{

	protected Type() {
		hashCode = curCount++;
	}
	
	private static int curCount = 1;
	private int hashCode;
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public SyntaxNode getDefinitionIfExists(){
		return null;
	}
	
	
	
	/**
	 * Returns the description of this type's category. (Not for the type itself)
	 * @return description of the type category.
	 */
	public abstract String getDescription();
	
	public abstract int getCategory();

	public static final int BASIC = 1;
	public static final int INTERFACE = 2;
	public static final int CLASS = 3;
	public static final int STRUCT = 4;
	public static final int POINTER = 5; 
	public static final int ENRICH = 6;
	public static final int ARRAY = 7;
	public static final int GENERIC_CLASS = 8;
	public static final int EXTENSION = 9;
	public static final int FUNCTION = 10;
	public static final int TYPE_PARAM = 11;
	public static final int OTHER = 0;
	
	
	public boolean canConcatenateCastTo(Type toType){if(toType==this) return true; return canImplicitCastTo(toType);}
	
	public boolean canImplicitCastTo(Type toType){if(toType==this) return true; return false;}

	public boolean canExplicitCastTo(Type toType) {if(toType==this) return true; return canImplicitCastTo(toType);}
	
	public boolean canBeNull(){ return true;}
	
	public abstract int getRuntimeType();
	
	public String getGeneratedName(){ return getUid();}
	
	/**
	 * Gets the name used to define this type.
	 * For most types, this is just the generated name.
	 * However, for classes, this is not a pointer to a class.
	 * @return
	 */
	public String getGeneratedDefinitionName(){ return getGeneratedName();}

	public String getDefaultValueStr() { return "null"; }

	public boolean canHaveFields() {
		return false;
	}
	
	/**
	 * States if this type is an implicit reference type, i.e., its values get
	 * passed by reference even if no pointer syntax is used.
	 * This is true for classes and interfaces.
	 * @return if this type is an implicit reference type.
	 */
	public boolean isImplicitReferenceType(){
		return false;
	}
	
	/**
	 * Returns if this type can be used as parameter (structs and arrays cannot)
	 * @return
	 */
	public boolean isValidAsParameter(){
		return true;
	}

	public boolean canHaveMethods() {
		return false;
	}
	
	/**
	 * Returns the type that is generated for this type. For most types, it is the type
	 * itself. For classes, it is the top class of the hierarchy.
	 * @return
	 */
	public Type getGeneratedType(){
		return this;
	}

	public Type getWrappedType() {
		return null;
	}
	
	/**
	 * For extensions this method returns the type the extension is based of.
	 * This is not its direct ancestor but the top of the extension hierarchy, i.e. a basic type.
	 * For all other types this function is the identity.
	 * @return the base type for extensions
	 */
	public Type getBaseType(){
		return this;
	}
	

	/**
	 * For extensions, this method returns the reachable base type.
	 * I.e. if this is a disjoint type, then the type itself is returned, otherwise the base type
	 * @return
	 */
	public Type getReachableBaseType(){
		return this;
	}
	
	
	public boolean isClass() {
		return false;
	}

	public boolean isGeneric() {
		return false;
	}
	
	public String getFullName(){
		return getUid();
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

	public boolean containsTypeParams() {
		return false;
	}

	public Type replaceTypeParameters(TypeParamMapping paramMap) {
		return this;
	}

	
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
	public Type getCommonSupertype(Type t){
		return this;
	}
	
	/**
	 * Returns the super type of this type or this type if it has no super type.
	 * @return super type or self
	 */
	public Type getSuperType(){
		return this;
	}
	
	
	protected Type getNthAncestor(int n){
		Type result = this;
		for(;n>0;n--){
			result = result.getSuperType();
		}
		return result;
	}

	
	/**
	 * Returns true if this type is the given type or a subtype of it
	 * @return
	 */
	public boolean isTypeOrSubtype(BasicType t){
		return this == t;
	}

	public abstract int getByteSize();

	/**
	 * Returns the byte size that this type has when being a member of a struct
	 */
	public int getMemberByteSize() {
		return getByteSize();
	}
	
	/**
	 * Returns true iff this is an integer type (currently only int and byte).
	 * @return
	 */
	public boolean isIntegerType(){
		return false;
	}
	
	/**
	 * Returns true iff this is a type extension declared with the iskey attribute.
	 * @return
	 */
	public boolean isKeyType(){
		return false;		
	}

	public DataObject getNextKey() {
		throw new Error("Keyof is not defined for the type " + this.getFullName());
	}
}
