package com.sc2mod.andromeda.environment.types.generic;

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.annotations.AnnotationSet;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.GenericContentSet;
import com.sc2mod.andromeda.environment.scopes.content.InheritableContentSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.impl.TypeImpl;
import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public abstract class GenericTypeInstance extends TypeImpl implements IDeclaredType , SemanticsVisitorNode {

	private IDeclaredType genericParent;
	private Signature typeArguments;
	private IType genericSuperType;
	private boolean copiedDown;
	private boolean hierarchyCopied;
	


	protected GenericTypeInstance(IDeclaredType genericParent, Signature s, TypeProvider t) {
		super(genericParent.getScope(), t);
		this.genericParent = genericParent;
		this.typeArguments = s;
		//We have to recreate the content set here because the first creation
		//did not work because the generic parent was not yet set.
		recreateContentSet();
	}
	
	protected ScopeContentSet createContentSet() {
		if(genericParent == null){
			return null;
		}
		return new GenericContentSet(genericParent,this);
	}

	public void setGenericSuperType(IType iType) {
		this.genericSuperType = iType;
	}

	
	public INamedType getGenericParent(){
		return genericParent;
	}
	

	public Signature getTypeArguments() {
		return typeArguments;
	}
	
	public void addContent(String name, IScopedElement elem) {
		throw new InternalProgramError("Cannot add content to generic instances");
	}

	public void addInheritedContent(IScope parentScope) {
		throw new InternalProgramError("Cannot add content to generic instances");
	}

	public TypeParameter[] getTypeParameters() {
		return genericParent.getTypeParameters();
	}
	
	@Override
	public boolean isGenericDecl() {
		return false;
	}
	
	@Override
	public boolean isGenericInstance() {
		return true;
	}
	
	public String getFullName() {
		return new StringBuilder().append(getUid()).append("<").append(typeArguments.getFullName()).append(">").toString();
	}
	
	@Override
	public boolean isSubtypeOf(IType t) {
		if(!hierarchyCopied){
			this.accept(new GenericHierachyGenerationVisitor(tprov));
		}
		if(t == this)
			return true;
		if(genericSuperType != null){
			return t == genericSuperType || genericSuperType.isSubtypeOf(t);
		}
		return false;
	}
	
	//*********** DELEGATED METHODS *******


	public boolean canBeNull() {
		return genericParent.canBeNull();
	}
	



	public boolean canHaveFields() {
		return genericParent.canHaveFields();
	}

	public boolean canHaveMethods() {
		return genericParent.canHaveMethods();
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


	public String getDefaultValueStr() {
		return genericParent.getDefaultValueStr();
	}

	public GlobalStructureNode getDefinition() {
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



	public String getGeneratedDefinitionName() {
		return genericParent.getGeneratedDefinitionName();
	}

	public String getGeneratedName() {
		return genericParent.getGeneratedName();
	}

	public IType getGeneratedType() {
		return genericParent.getGeneratedType();
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

	public boolean isValidAsParameter() {
		return genericParent.isValidAsParameter();
	}

	public void setGeneratedName(String generatedName) {
		genericParent.setGeneratedName(generatedName);
	}

	public void setTypeParameters(TypeParameter[] types) {
		genericParent.setTypeParameters(types);
	}

	public boolean isAbstract() {
		return genericParent.isAbstract();
	}

	public boolean isConst() {
		return genericParent.isConst();
	}

	public boolean isFinal() {
		return genericParent.isFinal();
	}

	public boolean isNative() {
		return genericParent.isNative();
	}

	public boolean isOverride() {
		return genericParent.isOverride();
	}

	public void setAbstract() {
		genericParent.setAbstract();
	}

	public void setConst() {
		genericParent.setConst();
	}

	public void setFinal() {
		genericParent.setFinal();
	}

	public void setNative() {
		genericParent.setNative();
	}

	public void setOverride() {
		genericParent.setOverride();
	}

	public void setStatic() {
		genericParent.setStatic();
	}

	public void setVisibility(Visibility visibility) {
		genericParent.setVisibility(visibility);
	}

	@Override
	public AnnotationSet getAnnotations(boolean createIfNotExistant) {
		return genericParent.getAnnotations(createIfNotExistant);
	}

	@Override
	public boolean hasCopiedDownContent(){
		return copiedDown;
	}
	 
	@Override
	public void setCopiedDownContent(){
		copiedDown = true;
	}

	public boolean isGenericHierarchyCopied() {
		return hierarchyCopied;
	}

	public void setGenericHierarchyCopied(boolean hierarchyCopied) {
		this.hierarchyCopied = hierarchyCopied;
	}

	
	

}
