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
import com.sc2mod.andromeda.environment.scopes.BlockScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.InheritableContentSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public interface IType extends IScope, IScopedElement{

		
	
	
	/**
	 * Returns the description of this type's category. (Not for the type itself)
	 * @return description of the type category.
	 */
	String getDescription();
	
	TypeCategory getCategory();


	
	
	boolean canConcatenateCastTo(IType toType);
	
	boolean canImplicitCastTo(IType toType);

	boolean canExplicitCastTo(IType toType);
	
	boolean canBeNull();
	
	int getRuntimeType();
	
	String getGeneratedName();
	
	/**
	 * Gets the name used to define this type.
	 * For most types, this is just the generated name.
	 * However, for classes, this is not a pointer to a class.
	 * @return
	 */
	 String getGeneratedDefinitionName();

	 String getDefaultValueStr();

	 boolean canHaveFields();
	
	/**
	 * States if this type is an implicit reference type, i.e., its values get
	 * passed by reference even if no pointer syntax is used.
	 * This is true for classes and interfaces.
	 * @return if this type is an implicit reference type.
	 */
	 boolean isImplicitReferenceType();
	
	/**
	 * Returns if this type can be used as parameter (structs and arrays cannot)
	 * @return
	 */
	 boolean isValidAsParameter();

	 boolean canHaveMethods();
	
	/**
	 * Returns the type that is generated for this type. For most types, it is the type
	 * itself. For classes, it is the top class of the hierarchy.
	 * @return
	 */
	 IType getGeneratedType();

	 IType getWrappedType();
	
	/**
	 * For extensions this method returns the type the extension is based of.
	 * This is not its direct ancestor but the top of the extension hierarchy, i.e. a basic type.
	 * For all other types this function is the identity.
	 * @return the base type for extensions
	 */
	 IType getBaseType();
	

	/**
	 * For extensions, this method returns the reachable base type.
	 * I.e. if this is a disjoint type, then the type itself is returned, otherwise the base type
	 * @return
	 */
	 IType getReachableBaseType();
	
	
	 boolean isClass();

	 boolean isGeneric();
	
	 String getFullName();

	 boolean containsTypeParams();

	//TODO: Factor out into visitor
	 IType replaceTypeParameters(TypeParamMapping paramMap);

	
	 boolean isTypeOrExtension(BasicType i);
	
	/**
	 * Returns the "lowest" supertype of this type and t that is shared
	 * by both. Only working for extensions at the moment. For every
	 * other type, this is returned.
	 * @param t the other type
	 * @return
	 */
	 IType getCommonSupertype(IType t);
	
	/**
	 * Returns the super type of this type or this type if it has no super type.
	 * @return super type or self
	 */
	 IType getSuperType();
	
	
	IType getNthAncestor(int n);

	
	/**
	 * Returns true if this type is the given type or a subtype of it
	 * @return
	 */
	 boolean isTypeOrSubtype(BasicType t);

	 int getByteSize();

	/**
	 * Returns the byte size that this type has when being a member of a struct
	 */
	 int getMemberByteSize();
	
	/**
	 * Returns true iff this is an integer type (currently only int and byte).
	 * @return
	 */
	 boolean isIntegerType();
	
	/**
	 * Returns true iff this is a type extension declared with the iskey attribute.
	 * @return
	 */
	 boolean isKeyType();

	 DataObject getNextKey();
	
	//*** SCOPING METHODS ***
	
	 void addInheritedContent(IScope parentScope);
	
	

}
