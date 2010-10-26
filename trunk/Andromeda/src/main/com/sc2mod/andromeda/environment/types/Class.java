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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassNameProvider;
import com.sc2mod.andromeda.environment.Annotations;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.syntaxNodes.TypeParamListNode;

import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Deallocator;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.MethodSet;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;

public class Class extends ReferentialType implements IIdentifiable , SemanticsVisitorNode {
	
	public static final int DEFAULT_CLASS_INSTANCE_LIMIT = 128;

	//XPilot: GenericClass should have access to (some of) these?

	/**
	 * The classes destructor. If this class has no own destructor, this
	 * will point to the destructor of the next super class that has a destructor.
	 * If no super class has one, it points to the deallocator.
	 */
	protected Destructor destructor;
	
	/**
	 * The constructors of this class.
	 */
	protected OperationSet constructors = new MethodSet(this, "<cons>");
	
	/**
	 * The name provider of this class
	 */
	protected ClassNameProvider nameProvider;
	
	/**
	 * The syntax node declaring this class
	 */
	protected ClassDeclNode declaration;
	
	/**
	 * The super class, or null if this is a top class.
	 */
	protected Class superClass;
	
	//TODO: Factor out? After all it isn't even used. maybe delete.
	private HashMap<String,Interface> interfacesTransClosure;
	
	protected int classIndex;
	protected int minInstanceofIndex;
	
	//TODO: Put this into the classNameProvider? After all, it is heavily related to name generation and code generation.
	private String allocatorName;
	
	//TODO: Factor out, this is heavily related with code generation.
	private ArrayList<VarDecl> hierarchyFields;
	
	/**
	 * The classes instance limit
	 */
	private int instanceLimit = DEFAULT_CLASS_INSTANCE_LIMIT;
	
	//TODO: Factor this out? The instanciation count has nothing todo with the class but with its environment.
	private int instantiationCount;
	
	//TODO: same as above
	private int indirectInstantiationCount;
	
	/**
	 * The virtual call table of this class.
	 */
	private VirtualCallTable virtualCallTable;

	/**
	 * Wether this class is declared static
	 */
	private boolean isStatic;
	
	/**
	 * The type parameters of this class
	 */
	protected TypeParameter[] typeParams;
	
	private static HashSet<String> allowedAnnotations = new HashSet<String>();
	
	@Override
	public ClassDeclNode getDefinition() {
		return declaration;
	}
	
	public OperationSet getConstructors() {
		return constructors;
	}
	
	public void addConstructor(Constructor c){
		constructors.add(c);
	}
	
	public Destructor getDestructor() {
		return destructor;
	}

	public void setDestructor(Destructor destructor) {
		if(this.destructor != null){
			throw Problem.ofType(ProblemId.DUPLICATE_DESTRUCTOR).at(this.destructor.getDefinition(),destructor.getDefinition())
						.raiseUnrecoverable();
		} else {
			this.destructor = destructor;
		}
	}
	static{
		if(RecordType.allowedAnnotations != null)
			allowedAnnotations.addAll(RecordType.allowedAnnotations);
		allowedAnnotations.add(Annotations.KEEP_AFTER_FOREACH);
	}
	
	@Override
	public void setStatic() {
		isStatic = true;
	}
	
	@Override
	public boolean isStatic() {
		return isStatic;
	}
	
	@Override
	public HashSet<String> getAllowedAnnotations() {
		return allowedAnnotations;
	}
	

	public VirtualCallTable getVirtualCallTable() {
		return virtualCallTable;
	}

	public void setVirtualCallTable(VirtualCallTable virtualCallTable) {
		this.virtualCallTable = virtualCallTable;
	}

	private String deallocatorName;

	private String metaClassName;
	
	public void setInstanceLimit(int instanceLimit) {
		this.instanceLimit = instanceLimit;
	}
	
	public int getInstanceLimit() {
		return instanceLimit;
	}

	public String getAllocatorName() {
		return allocatorName;
	}
	
	public void setAllocatorName(String allocatorName) {
		this.allocatorName = allocatorName;
	}
	
	public String getDeallocatorName(){
		return deallocatorName;
	}

	public void setDeallocatorName(String deallocatorName) {
		this.deallocatorName = deallocatorName;
		if(destructor instanceof Deallocator){
			destructor.setGeneratedName(deallocatorName);
		}
	}

	public int getClassIndex() {
		return classIndex;
	}
	
	public ArrayList<VarDecl> getHierarchyFields() {
		return hierarchyFields;
	}

	public void setHierarchyFields(ArrayList<VarDecl> hierarchyFields) {
		this.hierarchyFields = hierarchyFields;
	}

	public Class(ClassDeclNode declaration, Scope scope) {
		super(declaration, scope);
		this.nameProvider = new IndexClassNameProvider(this);
		interfacesTransClosure = new HashMap<String,Interface>();
		this.declaration = declaration;
		
		//XPilot: moved from GenericClass
		TypeParamListNode tl = declaration.getTypeParams();
		int size = tl == null ? 0 : tl.size();
		typeParams = new TypeParameter[size];
		for(int i=0;i<size;i++){
			typeParams[i] = new TypeParameter(this, tl.elementAt(i));
		}
	}
	
//	protected Class(Class genericParent){
//		super(genericParent);
//	}

	@Override
	public String getDescription() {
		return "class";
	}
	
	@Override
	public boolean canConcatenateCastTo(Type toType) {
		if(super.canConcatenateCastTo(toType)) return true;
		if(toType==BasicType.STRING || toType==BasicType.TEXT) return true;
		return false;
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.CLASS;
	}

//	@Override
////	public boolean isSuperclassOf(Class child) {
////		if(child.superClass==null) return false;
////		if(child.superClass==this) return true;
////		return isSuperclassOf(child.superClass);
////	}
	
	@Override
	public boolean isImplicitReferenceType() {
		return true;
	}

	@Override
	public boolean canHaveMethods() {
		return true;
	}

	void generateClassIndex(TypeProvider tp){
		minInstanceofIndex = tp.getCurInstanceofIndex();
		
		for(RecordType r: descendants){
			((Class)r).generateClassIndex(tp);
		}
		
		classIndex = tp.getNextClassIndex();
		
	}
	
	public boolean isTopClass(){
		return superClass == null;
	}

	public Class getTopClass() {
		if(superClass != null) return superClass.getTopClass();
		return this;
	}
	
	@Override
	public Type getGeneratedType() {
		return BasicType.INT;
	}
	
	@Override
	public String getGeneratedName() {
		return nameProvider.getName();
	}
	
	@Override
	public String getGeneratedDefinitionName() {
		return nameProvider.getName();
	}
	
	public ClassNameProvider getNameProvider(){
		return nameProvider;
	}
	
	@Override
	public void setGeneratedName(String generatedName) {
		nameProvider.setName(generatedName);
	}
	
	@Override
	public boolean isClass() {
		return true;
	}

	public Class getSuperClass() {
		return superClass;
	}
	
	

	
	@Override
	public boolean isInstanceof(Class c){
		//XPilot: changed implementation here, as it wasn't working properly...
		//return classIndex >= c.minInstanceofIndex && classIndex <= c.classIndex;
		if(this == c) return true;
		if(superClass == null) return false;
		return superClass.isInstanceof(c);
	}
	
	@Override
	public boolean canImplicitCastTo(Type toType) {
		if(toType==this) return true;
		//XPilot: Can now implicit cast to a generic type
		if(!toType.isClass()) return false;
		return (this.isInstanceof((Class)toType));
	}
	
	@Override
	public boolean canExplicitCastTo(Type toType) {
		if(toType==this) return true;
		if(toType.isTypeOrExtension(BasicType.INT)) return true;
		if(!toType.isClass()) return false;
		return ((Class)toType).isInstanceof(this)||(this.isInstanceof((Class)toType));
	}

	public void registerInstantiation() {
		instantiationCount++;
		if(superClass != null) superClass.registerIndirectInstantiation();
	}

	protected void registerIndirectInstantiation() {
		indirectInstantiationCount++;
		if(superClass != null) superClass.registerIndirectInstantiation();
	}
	
	/**
	 * After call hierarchy analysis, this method can state if a class
	 * is EVER used (including use by subclassing). If this method returns false then,
	 * the class and all of its subclasses are never instantiated. 
	 * 
	 * Any code for them can be omitted and virtual calls might be resolvable at compile time
	 * @return whether this class is ever used (including subclassing)
	 */
	public boolean isUsed() {
		return (instantiationCount + indirectInstantiationCount) > 0;
	}

	public void setMetaClassName(String name) {
		metaClassName = name;
	}

	public String getMetaClassName() {
		return metaClassName;
	}

	/**
	 * Generates the transitive closure for the implements relation which is needed for
	 * interface tables.
	 * A class interface pair is in this relation if either
	 * a) the class implements the interface directly
	 * b) the class extends a class which implements the interface
	 * c) the class implements an interface which extends the interface
	 * d) mixture of b) and c)
	 */
	public void generateImplementsTransClosure() {
	}

	/**
	 * Returns true if and only if the two handed types are classes and
	 * one is instanceof the other or vice versa.
	 * @param left a class
	 * @param right another class
	 */
	public static boolean isHierarchyShared(Class left, Class right) {
		return left.isInstanceof(right)||right.isInstanceof(left);
	}
	
	@Override
	protected int calcByteSize() {
		int result = 0;
		for(VarDecl f: getTopClass().hierarchyFields){
			result += f.getType().getMemberByteSize();
		}
		return result;
	}
	
	@Override
	public int getMemberByteSize() {
		return 4; //Classes are implicit ints
	}
	
	//XPilot: moved from GenericClass
	public TypeParameter[] getTypeParams(){
		return typeParams;
	}
	
	@Override
	public boolean isGeneric() {
		return false;
	}


	public void setSuperClass(Class type) {
		superClass = type;
	}


	public boolean hasConstructors() {
		return !constructors.isEmpty();
	}


	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
