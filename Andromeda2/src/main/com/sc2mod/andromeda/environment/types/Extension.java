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

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeExtension;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.IntObject;
import com.sc2mod.andromeda.vm.data.StringObject;

public class Extension extends BasicType{
	
	
	

	private TypeExtension definition;
	private Type extendedType;
	private BasicType extendedBaseType;
	private boolean isDistinct;
	private boolean isKey;
	private int hierarchyLevel;

 
	@Override
	public SyntaxNode getDefinitionIfExists() {
		return definition;
	}
	
	@Override
	public Type getBaseType() {
		return extendedBaseType;
	}
	
	@Override
	public String getDescription() {
		return  (isDistinct?"disjoint ":"hierarchic ") + "type extension";
	}
	
	@Override
	public Type getReachableBaseType() {
		if(isDistinct) return this;
		return extendedBaseType;
	}
	
	@Override
	public DataObject getNextKey() {
		if(extendedBaseType==BasicType.INT){
			return new IntObject(curKey++);
		} else if(extendedBaseType==BasicType.STRING) {
			return new StringObject(curKey++);
		} else {
			//This should not happen because getNextKey can only be called on keys, and these can only
			//be of type int or string
			throw new Error("Keyof on wrong base type! Bugreport please!");
		}
	}
	 
	
	public Extension(TypeExtension def, Type extendedType) {
		super(def.getName());
		isDistinct = def.getDisjoint();
		switch(extendedType.getCategory()){
		case BASIC:
			extendedBaseType = (BasicType)extendedType;
			hierarchyLevel = 0;
			break;
		case EXTENSION:
			Extension e = ((Extension)extendedType);
			extendedBaseType = e.extendedBaseType;
			hierarchyLevel = e.hierarchyLevel+1;
			if(isDistinct)
				throw Problem.ofType(ProblemId.DISJOINT_EXTENSION_BASED_ON_EXTENSION).at(def)
						.raiseUnrecoverable();
			break;
		default:
			throw Problem.ofType(ProblemId.EXTENSION_TYPE_INVALID).at(def)
						.details(extendedType.getFullName())
						.raiseUnrecoverable();
		}
		definition = def;
		this.extendedType = extendedType;
		this.isKey = def.getIsKey();
		
		if(extendedType.isKeyType()){
			throw Problem.ofType(ProblemId.EXTENSION_OF_KEY_TYPE).at(def)
					.raiseUnrecoverable();
		}
		if(isKey){
			if(extendedBaseType != BasicType.INT && extendedBaseType != BasicType.STRING){
				throw Problem.ofType(ProblemId.INVALID_BASE_TYPE_FOR_KEY_TYPE).at(def)
						.raiseUnrecoverable();
			}
		}
		
	}
	
	@Override
	public boolean isKeyType() {
		return isKey;
	}

	@Override
	public int getCategory() {
		return EXTENSION;
	}
	
	@Override
	public String getGeneratedDefinitionName() {
		return extendedBaseType.getGeneratedDefinitionName();
	}
	
	@Override
	public String getGeneratedName() {
		return extendedBaseType.getGeneratedName();
	}
	
	@Override
	public Type getGeneratedType() {
		return extendedBaseType;
	}
	
	@Override
	public boolean isTypeOrExtension(BasicType i) {
		return extendedBaseType==i;
	}
	
	@Override
	public boolean isTypeOrSubtype(BasicType t) {
		if(isDistinct) return false;
		return extendedBaseType==t;
	}
	
	@Override
	public boolean canConcatenateCastTo(Type toType) {
		return extendedBaseType.canConcatenateCastTo(toType);
	}
	
	@Override
	public boolean canImplicitCastTo(Type toType) {
		if(toType==this) return true;
		if(isDistinct) return false;
		return extendedType.canImplicitCastTo(toType);
	}
	
	@Override
	public boolean canExplicitCastTo(Type toType) {
		return extendedBaseType.canExplicitCastTo(toType);
	}
	
	@Override
	public int getRuntimeType() {
		return extendedBaseType.getRuntimeType();
	}
	
	
	@Override
	public Type getCommonSupertype(Type t) {

		if(t == this) return this;
		switch(t.getCategory()){
		case BASIC: 
			return extendedBaseType.getCommonSupertype(t);
		case EXTENSION:
			Extension e = (Extension)t;
			if(e.extendedBaseType != extendedBaseType) return e.extendedBaseType.getCommonSupertype(extendedBaseType);
			return getCommonInternal(t,((Extension)t).hierarchyLevel);
		}
		throw new Error("Common supertype error");
	}
	
	private Type getCommonInternal(Type t, int hierarchyLevel2) {

		if(hierarchyLevel2 == hierarchyLevel){
			return getCommonInternal2(this,t);
		} else if(hierarchyLevel2 < hierarchyLevel){
			return getCommonInternal2(this.getNthAncestor(hierarchyLevel-hierarchyLevel2),t);
		} else {
			return getCommonInternal2(this,t.getNthAncestor(hierarchyLevel2-hierarchyLevel));	
		}
	}

	private static Type getCommonInternal2(Type  t1, Type t2) {
		if(t1 == t2) return t1;
		Type sup = t1.getSuperType();
		if(sup == t1) return t1;
		return getCommonInternal2(sup, t2.getSuperType());
	}

	@Override
	public Type getSuperType() {
		return extendedType;
	}

	
	
	
	
}
