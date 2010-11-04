package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Extension;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public class GenericExtensionInstance extends GenericTypeInstance {

	private final Extension genericType;

	public GenericExtensionInstance(Extension i, Signature s){
		super(i,s);
		this.genericType = i;
		
	}

	public void addContent(String name, IScopedElement elem) {
		genericType.addContent(name, elem);
	}

	public void addInheritedContent(IScope parentScope) {
		genericType.addInheritedContent(parentScope);
	}

	public boolean canBeNull() {
		return genericType.canBeNull();
	}

	public boolean canConcatenateCastTo(IType toType) {
		return genericType.canConcatenateCastTo(toType);
	}

	public boolean canExplicitCastTo(IType toType) {
		return genericType.canExplicitCastTo(toType);
	}

	public boolean canHaveFields() {
		return genericType.canHaveFields();
	}

	public boolean canHaveMethods() {
		return genericType.canHaveMethods();
	}

	public boolean canImplicitCastTo(IType toType) {
		return genericType.canImplicitCastTo(toType);
	}

	public boolean containsTypeParams() {
		return genericType.containsTypeParams();
	}

	public ScopeContentSet createContentSet() {
		return genericType.createContentSet();
	}

	public boolean equals(Object obj) {
		return genericType.equals(obj);
	}

	public BasicType getBaseType() {
		return genericType.getBaseType();
	}

	public int getByteSize() {
		return genericType.getByteSize();
	}

	public TypeCategory getCategory() {
		return genericType.getCategory();
	}

	public IType getCommonSupertype(IType t) {
		return genericType.getCommonSupertype(t);
	}

	public IType getContainingType() {
		return genericType.getContainingType();
	}

	public ScopeContentSet getContent() {
		return genericType.getContent();
	}

	public String getDefaultValueStr() {
		return genericType.getDefaultValueStr();
	}

	public TypeExtensionDeclNode getDefinition() {
		return genericType.getDefinition();
	}

	public String getDescription() {
		return genericType.getDescription();
	}

	public ScopedElementType getElementType() {
		return genericType.getElementType();
	}

	public String getElementTypeName() {
		return genericType.getElementTypeName();
	}

	public int getExtensionHierachryLevel() {
		return genericType.getExtensionHierachryLevel();
	}

	public String getFullName() {
		return genericType.getFullName();
	}

	public String getGeneratedDefinitionName() {
		return genericType.getGeneratedDefinitionName();
	}

	public String getGeneratedName() {
		return genericType.getGeneratedName();
	}

	public IType getGeneratedType() {
		return genericType.getGeneratedType();
	}

	public INamedType getGenericInstance(Signature s) {
		return genericType.getGenericInstance(s);
	}

	public int getMemberByteSize() {
		return genericType.getMemberByteSize();
	}

	public String getName() {
		return genericType.getName();
	}

	public DataObject getNextKey() {
		return genericType.getNextKey();
	}

	public Package getPackage() {
		return genericType.getPackage();
	}

	public IScope getParentScope() {
		return genericType.getParentScope();
	}

	public IType getReachableBaseType() {
		return genericType.getReachableBaseType();
	}

	public int getRuntimeType() {
		return genericType.getRuntimeType();
	}

	public IScope getScope() {
		return genericType.getScope();
	}

	public IType getSuperType() {
		return genericType.getSuperType();
	}

	public String getUid() {
		return genericType.getUid();
	}

	public Visibility getVisibility() {
		return genericType.getVisibility();
	}

	public IType getWrappedType() {
		return genericType.getWrappedType();
	}

	public int hashCode() {
		return genericType.hashCode();
	}

	public boolean isClass() {
		return genericType.isClass();
	}

	public boolean isDistinct() {
		return genericType.isDistinct();
	}

	public boolean isGeneric() {
		return genericType.isGeneric();
	}

	public boolean isGenericDecl() {
		return genericType.isGenericDecl();
	}

	public boolean isImplicitReferenceType() {
		return genericType.isImplicitReferenceType();
	}

	public boolean isIntegerType() {
		return genericType.isIntegerType();
	}

	public boolean isKey() {
		return genericType.isKey();
	}

	public boolean isKeyType() {
		return genericType.isKeyType();
	}

	public boolean isStatic() {
		return genericType.isStatic();
	}

	public boolean isTypeOrExtension(BasicType i) {
		return genericType.isTypeOrExtension(i);
	}

	public boolean isTypeOrSubtype(BasicType t) {
		return genericType.isTypeOrSubtype(t);
	}

	public boolean isValidAsParameter() {
		return genericType.isValidAsParameter();
	}

	public IType replaceTypeParameters(TypeParamMapping paramMap) {
		return genericType.replaceTypeParameters(paramMap);
	}

	public void setGeneratedName(String generatedName) {
		genericType.setGeneratedName(generatedName);
	}

	public void setResolvedExtendedType(IType extendedType2,
			BasicType extendedBaseType2, int hierarchyLevel2) {
		genericType.setResolvedExtendedType(extendedType2, extendedBaseType2,
				hierarchyLevel2);
	}

	public void setTypeParameters(TypeParameter[] types) {
		genericType.setTypeParameters(types);
	}

	public String toString() {
		return genericType.toString();
	}
	
	
	
	
	
	
}
