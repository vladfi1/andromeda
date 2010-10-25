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
import java.util.List;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassNameProvider;
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Annotations;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.Deallocator;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.syntaxNodes.TypeParamListNode;

public class Class extends RecordType implements IIdentifiable {
	
	public static final int DEFAULT_CLASS_INSTANCE_LIMIT = 128;

	//XPilot: GenericClass should have access to (some of) these?
	protected ClassNameProvider nameProvider;
	protected ClassDeclNode declaration;
	protected Class superClass;
	protected Class topClass;
	protected HashMap<String,Interface> interfaces;
	private HashMap<String,Interface> interfacesTransClosure;
	protected int classIndex;
	private String allocatorName;
	protected int minInstanceofIndex;
	private ArrayList<FieldDecl> hierarchyFields;
	private int instanceLimit = DEFAULT_CLASS_INSTANCE_LIMIT;
	private int instantiationCount;
	private int indirectInstantiationCount;
	private VirtualCallTable virtualCallTable;

	private boolean isStatic;
	
	//XPilot: added
	protected TypeParameter[] typeParams;
	
	private static HashSet<String> allowedAnnotations = new HashSet<String>();
	static{
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
	
	public ArrayList<FieldDecl> getHierarchyFields() {
		return hierarchyFields;
	}

	public void setHierarchyFields(ArrayList<FieldDecl> hierarchyFields) {
		this.hierarchyFields = hierarchyFields;
	}

	public Class(ClassDeclNode declaration, Scope scope) {
		super(declaration, scope);
		this.nameProvider = new IndexClassNameProvider(this);
		interfaces = new HashMap<String,Interface>();
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
	
	protected Class(Class genericParent){
		super(genericParent);
	}

	@Override
	public String getDescription() {
		return "Class, defined at:\n" + CompilationFileManager.getLastEnvironment().getSourceInformation(this.getDeclaration());
	}

	/**
	 * XPilot: enabled extending of generic classes.
	 */
	protected void resolveExtends(TypeProvider t) {
		t.pushTypeParams(typeParams);
		Type type = t.resolveType(declaration.getSuperClass());
		t.popTypeParams(typeParams);
		if(!type.isClass())
			throw Problem.ofType(ProblemId.CLASS_EXTENDS_NON_CLASS).at(declaration.getSuperClass())
							.raiseUnrecoverable();
		
		superClass = (Class)type;
	}

	protected void resolveImplements(TypeProvider t) {
		TypeListNode tl = declaration.getInterfaces();
		int size = tl.size();
		for(int i=0;i<size;i++){
			Type in = t.resolveType(tl.elementAt(i));
			if(in.getCategory()!=INTERFACE){
				throw Problem.ofType(ProblemId.CLASS_IMPLEMENTS_NON_INTERFACE).at(tl.elementAt(i))
						.raiseUnrecoverable();
			}
			Type old = interfaces.put(in.getUid(), (Interface) in);
			if(old != null){
				throw Problem.ofType(ProblemId.DUPLICATE_IMPLEMENTS).at(tl.elementAt(i))
						.raiseUnrecoverable();
			}
		}		
		if(isStatic)
			throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_IMPLEMENTS).at(declaration.getInterfaces())
						.raiseUnrecoverable();
	}
	
	@Override
	public boolean resolveInheritance(TypeProvider t) {
		if(!super.resolveInheritance(t))return false;

		TypeListNode in = declaration.getInterfaces();
		if(in!=null&&!in.isEmpty())resolveImplements(t);
		if(declaration.getSuperClass() != null)resolveExtends(t);
		return true;
		
	}
	
	@Override
	public boolean canConcatenateCastTo(Type toType) {
		if(super.canConcatenateCastTo(toType)) return true;
		if(toType==BasicType.STRING || toType==BasicType.TEXT) return true;
		return false;
	}

	@Override
	public int getCategory() {
		return CLASS;
	}

	@Override
	protected void checkForHierarchyCircle(TypeProvider typeProvider,HashSet<RecordType> marked) {
		
		//Build hierarchy
		if(!hierarchyChecked){
			boolean hasParents = false;
			for(String s:interfaces.keySet()){
				Interface i = interfaces.get(s);
				i.descendants.add(this);
				hasParents = true;
			}
			if(superClass!=null){
				if(superClass.isFinal())
					throw Problem.ofType(ProblemId.FINAL_CLASS_EXTENDED).at(this.declaration.getSuperClass())
								.details(superClass.getName())
								.raiseUnrecoverable();
				if(isStatic)
					throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_EXTENDS).at(this.declaration.getSuperClass())
								.raiseUnrecoverable();
				if(superClass.isStatic)
					throw Problem.ofType(ProblemId.STATIC_CLASS_EXTENDED).at(this.declaration.getSuperClass())
								.raiseUnrecoverable();
				superClass.descendants.add(this);
				hasParents = true;
			}
			if(!hasParents) typeProvider.addRootRecord(this);
		}
		
		//Do circle checking
		doHierarchyCheck(this, marked);
		for(String s:interfaces.keySet()){
			interfaces.get(s).checkForHierarchyCircle(typeProvider,marked);
		}
		if(superClass!=null){
			superClass.checkForHierarchyCircle(typeProvider,marked);
		}
		
	}

	@Override
	public void checkHierarchy(TypeProvider typeProvider) {
		if(hierarchyChecked) return;
		checkForHierarchyCircle(typeProvider,new HashSet<RecordType>());
	}
	
	@Override
	void resolveMembers(TypeProvider t) {
		//Top class
		Class topClass = this;
		while(topClass.superClass != null) topClass = topClass.superClass;
		this.topClass = topClass;
		
		for(String s: interfaces.keySet()){
			Interface i = interfaces.get(s);
			fields.addInheritedFields(i.fields);
			methods.addInheritedMethods(i.methods);
		}
		if(superClass!=null){
			fields.addInheritedFields(superClass.fields);
			methods.addInheritedMethods(superClass.methods);
		}
		
		super.resolveMembers(t);
		
		if(superClass!=null){
			if(destructor == null)
			{
				destructor = superClass.destructor;
			} else {
				superClass.destructor.addOverride(destructor);
			}
		} else {
			if(destructor == null){
				destructor = Deallocator.createDeallocator(t,this);		
			}
			
		}
		
		if(isStatic){
			if(!constructors.isEmpty())
				throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_CONSTRUCTOR).at(constructors.entrySet().iterator().next().getValue().getDefinition())
							.raiseUnrecoverable();
			
			if(getNumNonStatics()!=0)
				throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_NON_STATIC_MEMBER).at(declaration)
							.raiseUnrecoverable();

		}
		
		if(methods.containsUnimplementedMethods()&&!this.isAbstract()){
			List<AbstractFunction> meths = methods.getUnimplementedMethods();
			StringBuffer sb = new StringBuffer(256);
			for(AbstractFunction m : meths){
				sb.append("\n").append(m.getContainingType().getUid()).append(".").append(m.getNameAndSignature()).append("\n");
			}
			sb.setLength(sb.length()-1);
			throw Problem.ofType(ProblemId.ABSTRACT_CLASS_MISSES_IMPLEMENTATIONS).at(this.declaration)
							.details(this.getUid(),sb.toString())
							.raiseUnrecoverable();
			
		}
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
		return topClass == this;
	}

	public Class getTopClass() {
		return topClass;
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
	
	public Constructor resolveConstructorCall(SyntaxNode call,Signature s, int maxVisibility) {
		//Direct hit?
		Constructor c = constructors.get(s);
		if(c != null){
			//Not visible :(
			if(c.getVisibility()>maxVisibility) c = null;
			else return c;
		}
		Signature s2 = constructors.entrySet().iterator().next().getKey();
		
		
		//No direct hit
		Signature candidate = null;
		for(Signature sig: constructors.keySet()){
			if(s.canImplicitCastTo(sig)){
				c = constructors.get(sig);
				if(c.getVisibility()<=maxVisibility){
					if(candidate != null) 
						throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_CALL)
								.at(call)
								.details("constructor",constructors.get(candidate).getNameAndSignature(),constructors.get(sig).getNameAndSignature())
								.raiseUnrecoverable();
						
					candidate = sig;	
				}
			}
		}
		if(candidate == null) return null;
		return constructors.get(candidate);
	}
	


	public boolean hasFieldInit(){
		return fields.numNonStaticInits()>0;
	}

	public void checkImplicitConstructor() {
		if(isTopClass()) return;
		if(hasConstructors()) return;
		
		Class superClass = this.superClass;
		if(!superClass.hasConstructors()) return;
		if(null==superClass.resolveConstructorCall(declaration, Signature.EMPTY_SIGNATURE, Visibility.PROTECTED))
			throw Problem.ofType(ProblemId.CONSTRUCTOR_REQUIRED).at(declaration)
						.details(superClass.getName(),this.getName())
						.raiseUnrecoverable();
		
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
		for(FieldDecl f: topClass.hierarchyFields){
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
}
