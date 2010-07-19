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

//XPilot: what is this? :)
//import javax.management.relation.RoleUnresolved;

import com.sc2mod.andromeda.syntaxNodes.TypeParam;

/**
 * A generic type parameter.
 * @author J. 'gex' Finis
 *
 */
public class TypeParameter extends Type {

	private TypeParam decl;
	private String name;
	private Class forClass;
	
	public TypeParameter(Class forClass, TypeParam elementAt) {
		decl = elementAt;
		this.forClass = forClass;
		name = elementAt.getName();
	}

	@Override
	public int getCategory() {
		return TYPE_PARAM;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public String getUid() {
		return name;
	}
	
	@Override
	public String getFullName() {
		return forClass.getUid() + "::" + name;
	}
	

	/**
	 * A type parameter always contains type parameters (itself)
	 */
	@Override
	public boolean containsTypeParams() {
		return true;
	}
	
	@Override
	public Type replaceTypeParameters(TypeParamMapping paramMap) {
		return paramMap.getReplacement(this);
	}
	
	@Override
	public String getGeneratedName() {
		return BasicType.INT.getGeneratedName();
	}
	
	@Override
	public boolean canExplicitCastTo(Type toType) {
		if(toType==this) return true;
		if(toType.isTypeOrExtension(BasicType.INT)) return true; 
		if(toType.getCategory()==TYPE_PARAM)return true;
		if(toType.getCategory()==CLASS) return true;
		return false;
	}

	
	public int getRuntimeType() {
		return RuntimeType.OTHER;
	}
	
	@Override
	public String getDefaultValueStr() {
		return "0";
	}
	
	@Override
	public int getByteSize() {
		throw new Error("Getting byte size of params not supported!");
	}
}
