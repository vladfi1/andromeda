package com.sc2mod.andromeda.environment.types.generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.GenericClass;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.environment.types.impl.NamedTypeImpl;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public class GenericStructInstance extends GenericTypeInstance implements IStruct{

	private IStruct genericParent;

	public GenericStructInstance(IStruct struct, Signature s) {
		super(struct,s);
		this.genericParent = struct;
	}

	/**
	 * @param name
	 * @param elem
	 * @see com.sc2mod.andromeda.environment.scopes.BlockScope#addContent(java.lang.String, com.sc2mod.andromeda.environment.scopes.IScopedElement)
	 */
	public void addContent(String name, IScopedElement elem) {
		genericParent.addContent(name, elem);
	}

	/**
	 * @param parentScope
	 * @see com.sc2mod.andromeda.environment.types.IType#addInheritedContent(com.sc2mod.andromeda.environment.scopes.IScope)
	 */
	public void addInheritedContent(IScope parentScope) {
		genericParent.addInheritedContent(parentScope);
	}

	/**
	 * 
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#afterAnnotationsProcessed()
	 */
	public void afterAnnotationsProcessed() {
		genericParent.afterAnnotationsProcessed();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#canBeNull()
	 */
	public boolean canBeNull() {
		return genericParent.canBeNull();
	}

	/**
	 * @param toType
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#canConcatenateCastTo(com.sc2mod.andromeda.environment.types.IType)
	 */
	public boolean canConcatenateCastTo(IType toType) {
		return genericParent.canConcatenateCastTo(toType);
	}

	/**
	 * @param toType
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#canExplicitCastTo(com.sc2mod.andromeda.environment.types.IType)
	 */
	public boolean canExplicitCastTo(IType toType) {
		return genericParent.canExplicitCastTo(toType);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#canHaveFields()
	 */
	public boolean canHaveFields() {
		return genericParent.canHaveFields();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#canHaveMethods()
	 */
	public boolean canHaveMethods() {
		return genericParent.canHaveMethods();
	}

	/**
	 * @param toType
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#canImplicitCastTo(com.sc2mod.andromeda.environment.types.IType)
	 */
	public boolean canImplicitCastTo(IType toType) {
		return genericParent.canImplicitCastTo(toType);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#containsTypeParams()
	 */
	public boolean containsTypeParams() {
		return genericParent.containsTypeParams();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#createContentSet()
	 */
	public ScopeContentSet createContentSet() {
		return genericParent.createContentSet();
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return genericParent.equals(obj);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getAllowedAnnotations()
	 */
	public HashSet<String> getAllowedAnnotations() {
		return genericParent.getAllowedAnnotations();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getBaseType()
	 */
	public IType getBaseType() {
		return genericParent.getBaseType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getByteSize()
	 */
	public int getByteSize() {
		return genericParent.getByteSize();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IStruct#getCategory()
	 */
	public TypeCategory getCategory() {
		return genericParent.getCategory();
	}

	/**
	 * @param t
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getCommonSupertype(com.sc2mod.andromeda.environment.types.IType)
	 */
	public IType getCommonSupertype(IType t) {
		return genericParent.getCommonSupertype(t);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getContainingType()
	 */
	public IType getContainingType() {
		return genericParent.getContainingType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.scopes.IScope#getContent()
	 */
	public ScopeContentSet getContent() {
		return genericParent.getContent();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getDecendants()
	 */
	public LinkedList<RecordTypeImpl> getDecendants() {
		return genericParent.getDecendants();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getDefaultValueStr()
	 */
	public String getDefaultValueStr() {
		return genericParent.getDefaultValueStr();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IStruct#getDefinition()
	 */
	public StructDeclNode getDefinition() {
		return genericParent.getDefinition();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getDescendants()
	 */
	public LinkedList<RecordTypeImpl> getDescendants() {
		return genericParent.getDescendants();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IStruct#getDescription()
	 */
	public String getDescription() {
		return genericParent.getDescription();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getElementType()
	 */
	public ScopedElementType getElementType() {
		return genericParent.getElementType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getElementTypeName()
	 */
	public String getElementTypeName() {
		return genericParent.getElementTypeName();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getFullName()
	 */
	public String getFullName() {
		return genericParent.getFullName();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getGeneratedDefinitionName()
	 */
	public String getGeneratedDefinitionName() {
		return genericParent.getGeneratedDefinitionName();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.INamedType#getGeneratedName()
	 */
	public String getGeneratedName() {
		return genericParent.getGeneratedName();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getGeneratedType()
	 */
	public IType getGeneratedType() {
		return genericParent.getGeneratedType();
	}

	/**
	 * @param s
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getGenericInstance(com.sc2mod.andromeda.environment.Signature)
	 */
	public GenericClass getGenericInstance(Signature s) {
		return genericParent.getGenericInstance(s);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getMemberByteSize()
	 */
	public int getMemberByteSize() {
		return genericParent.getMemberByteSize();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getName()
	 */
	public String getName() {
		return genericParent.getName();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getNextKey()
	 */
	public DataObject getNextKey() {
		return genericParent.getNextKey();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getNumNonStatics()
	 */
	public int getNumNonStatics() {
		return genericParent.getNumNonStatics();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getNumStatics()
	 */
	public int getNumStatics() {
		return genericParent.getNumStatics();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.scopes.BlockScope#getPackage()
	 */
	public Package getPackage() {
		return genericParent.getPackage();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.scopes.BlockScope#getParentScope()
	 */
	public IScope getParentScope() {
		return genericParent.getParentScope();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getReachableBaseType()
	 */
	public IType getReachableBaseType() {
		return genericParent.getReachableBaseType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getRuntimeType()
	 */
	public int getRuntimeType() {
		return genericParent.getRuntimeType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getScope()
	 */
	public IScope getScope() {
		return genericParent.getScope();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getSuperType()
	 */
	public IType getSuperType() {
		return genericParent.getSuperType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getTypeParams()
	 */
	public TypeParameter[] getTypeParams() {
		return genericParent.getTypeParams();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getUid()
	 */
	public String getUid() {
		return genericParent.getUid();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#getVisibility()
	 */
	public Visibility getVisibility() {
		return genericParent.getVisibility();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#getWrappedType()
	 */
	public IType getWrappedType() {
		return genericParent.getWrappedType();
	}

	/**
	 * @param name
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#hasAnnotation(java.lang.String)
	 */
	public boolean hasAnnotation(String name) {
		return genericParent.hasAnnotation(name);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#hashCode()
	 */
	public int hashCode() {
		return genericParent.hashCode();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isAbstract()
	 */
	public boolean isAbstract() {
		return genericParent.isAbstract();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#isClass()
	 */
	public boolean isClass() {
		return genericParent.isClass();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isConst()
	 */
	public boolean isConst() {
		return genericParent.isConst();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isFinal()
	 */
	public boolean isFinal() {
		return genericParent.isFinal();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isGeneric()
	 */
	public boolean isGeneric() {
		return genericParent.isGeneric();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.INamedType#isGenericDecl()
	 */
	public boolean isGenericDecl() {
		return genericParent.isGenericDecl();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#isImplicitReferenceType()
	 */
	public boolean isImplicitReferenceType() {
		return genericParent.isImplicitReferenceType();
	}

	/**
	 * @param curClass
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isInstanceof(com.sc2mod.andromeda.environment.types.IClass)
	 */
	public boolean isInstanceof(IClass curClass) {
		return genericParent.isInstanceof(curClass);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#isIntegerType()
	 */
	public boolean isIntegerType() {
		return genericParent.isIntegerType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#isKeyType()
	 */
	public boolean isKeyType() {
		return genericParent.isKeyType();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isNative()
	 */
	public boolean isNative() {
		return genericParent.isNative();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isOverride()
	 */
	public boolean isOverride() {
		return genericParent.isOverride();
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#isStatic()
	 */
	public boolean isStatic() {
		return genericParent.isStatic();
	}

	/**
	 * @param i
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#isTypeOrExtension(com.sc2mod.andromeda.environment.types.BasicType)
	 */
	public boolean isTypeOrExtension(BasicType i) {
		return genericParent.isTypeOrExtension(i);
	}

	/**
	 * @param t
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#isTypeOrSubtype(com.sc2mod.andromeda.environment.types.BasicType)
	 */
	public boolean isTypeOrSubtype(BasicType t) {
		return genericParent.isTypeOrSubtype(t);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IStruct#isValidAsParameter()
	 */
	public boolean isValidAsParameter() {
		return genericParent.isValidAsParameter();
	}

	/**
	 * @param paramMap
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.IType#replaceTypeParameters(com.sc2mod.andromeda.environment.types.TypeParamMapping)
	 */
	public IType replaceTypeParameters(TypeParamMapping paramMap) {
		return genericParent.replaceTypeParameters(paramMap);
	}

	/**
	 * 
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setAbstract()
	 */
	public void setAbstract() {
		genericParent.setAbstract();
	}

	/**
	 * @param annotations
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setAnnotationTable(java.util.HashMap)
	 */
	public void setAnnotationTable(HashMap<String, AnnotationNode> annotations) {
		genericParent.setAnnotationTable(annotations);
	}

	/**
	 * 
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setConst()
	 */
	public void setConst() {
		genericParent.setConst();
	}

	/**
	 * 
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setFinal()
	 */
	public void setFinal() {
		genericParent.setFinal();
	}

	/**
	 * @param generatedName
	 * @see com.sc2mod.andromeda.environment.types.INamedType#setGeneratedName(java.lang.String)
	 */
	public void setGeneratedName(String generatedName) {
		genericParent.setGeneratedName(generatedName);
	}

	/**
	 * 
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setNative()
	 */
	public void setNative() {
		genericParent.setNative();
	}

	/**
	 * 
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setOverride()
	 */
	public void setOverride() {
		genericParent.setOverride();
	}

	/**
	 * 
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setStatic()
	 */
	public void setStatic() {
		genericParent.setStatic();
	}

	/**
	 * @param types
	 * @see com.sc2mod.andromeda.environment.types.INamedType#setTypeParameters(com.sc2mod.andromeda.environment.types.generic.TypeParameter[])
	 */
	public void setTypeParameters(TypeParameter[] types) {
		genericParent.setTypeParameters(types);
	}

	/**
	 * @param newVisibility
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#setVisibility(com.sc2mod.andromeda.environment.scopes.Visibility)
	 */
	public void setVisibility(Visibility newVisibility) {
		genericParent.setVisibility(newVisibility);
	}

	/**
	 * @return
	 * @see com.sc2mod.andromeda.environment.types.RecordTypeImpl#toString()
	 */
	public String toString() {
		return genericParent.toString();
	}
	
	

}
