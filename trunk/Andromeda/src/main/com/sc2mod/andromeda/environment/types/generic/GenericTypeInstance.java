package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.environment.types.impl.NamedTypeImpl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public abstract class GenericTypeInstance implements INamedType {

	private INamedType genericParent;
	
	protected GenericTypeInstance(INamedType genericParent, Signature s) {
		this.genericParent = genericParent;
	}

	public void addContent(String name, IScopedElement elem) {
		genericParent.addContent(name, elem);
	}

	public void addInheritedContent(IScope parentScope) {
		genericParent.addInheritedContent(parentScope);
	}

	public boolean canBeNull() {
		return genericParent.canBeNull();
	}

	public boolean canConcatenateCastTo(IType toType) {
		return genericParent.canConcatenateCastTo(toType);
	}

	public boolean canExplicitCastTo(IType toType) {
		return genericParent.canExplicitCastTo(toType);
	}

	public boolean canHaveFields() {
		return genericParent.canHaveFields();
	}

	public boolean canHaveMethods() {
		return genericParent.canHaveMethods();
	}

	public boolean canImplicitCastTo(IType toType) {
		return genericParent.canImplicitCastTo(toType);
	}

	public boolean containsTypeParams() {
		return genericParent.containsTypeParams();
	}

	public ScopeContentSet createContentSet() {
		return genericParent.createContentSet();
	}

	public INamedType createGenericInstance(Signature s) {
		return genericParent.createGenericInstance(s);
	}

	public IType getBaseType() {
		return genericParent.getBaseType();
	}

	public int getByteSize() {
		return genericParent.getByteSize();
	}

	public TypeCategory getCategory() {
		return genericParent.getCategory();
	}

	public IType getCommonSupertype(IType t) {
		return genericParent.getCommonSupertype(t);
	}

	public IType getContainingType() {
		return genericParent.getContainingType();
	}

	public ScopeContentSet getContent() {
		return genericParent.getContent();
	}

	public String getDefaultValueStr() {
		return genericParent.getDefaultValueStr();
	}

	public SyntaxNode getDefinition() {
		return genericParent.getDefinition();
	}

	public String getDescription() {
		return genericParent.getDescription();
	}

	public ScopedElementType getElementType() {
		return genericParent.getElementType();
	}

	public String getElementTypeName() {
		return genericParent.getElementTypeName();
	}

	public String getFullName() {
		return genericParent.getFullName();
	}

	public String getGeneratedDefinitionName() {
		return genericParent.getGeneratedDefinitionName();
	}

	public String getGeneratedName() {
		return genericParent.getGeneratedName();
	}

	public IType getGeneratedType() {
		return genericParent.getGeneratedType();
	}

	public INamedType getGenericInstance(Signature s) {
		return genericParent.getGenericInstance(s);
	}

	public int getMemberByteSize() {
		return genericParent.getMemberByteSize();
	}

	public String getName() {
		return genericParent.getName();
	}

	public DataObject getNextKey() {
		return genericParent.getNextKey();
	}

	public IType getNthAncestor(int n) {
		return genericParent.getNthAncestor(n);
	}

	public Package getPackage() {
		return genericParent.getPackage();
	}

	public IScope getParentScope() {
		return genericParent.getParentScope();
	}

	public IType getReachableBaseType() {
		return genericParent.getReachableBaseType();
	}

	public int getRuntimeType() {
		return genericParent.getRuntimeType();
	}

	public IScope getScope() {
		return genericParent.getScope();
	}

	public IType getSuperType() {
		return genericParent.getSuperType();
	}

	public String getUid() {
		return genericParent.getUid();
	}

	public Visibility getVisibility() {
		return genericParent.getVisibility();
	}

	public IType getWrappedType() {
		return genericParent.getWrappedType();
	}

	public boolean isClass() {
		return genericParent.isClass();
	}

	public boolean isGeneric() {
		return genericParent.isGeneric();
	}

	public boolean isGenericDecl() {
		return genericParent.isGenericDecl();
	}

	public boolean isImplicitReferenceType() {
		return genericParent.isImplicitReferenceType();
	}

	public boolean isIntegerType() {
		return genericParent.isIntegerType();
	}

	public boolean isKeyType() {
		return genericParent.isKeyType();
	}

	public boolean isStatic() {
		return genericParent.isStatic();
	}

	public boolean isTypeOrExtension(BasicType i) {
		return genericParent.isTypeOrExtension(i);
	}

	public boolean isTypeOrSubtype(BasicType t) {
		return genericParent.isTypeOrSubtype(t);
	}

	public boolean isValidAsParameter() {
		return genericParent.isValidAsParameter();
	}

	public IType replaceTypeParameters(TypeParamMapping paramMap) {
		return genericParent.replaceTypeParameters(paramMap);
	}

	public void setGeneratedName(String generatedName) {
		genericParent.setGeneratedName(generatedName);
	}

	public void setTypeParameters(TypeParameter[] types) {
		genericParent.setTypeParameters(types);
	}
	
	

}
