package com.sc2mod.andromeda.environment.types.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassNameProvider;
import com.sc2mod.andromeda.environment.Annotations;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.content.MethodSet;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.generic.GenericClassInstance;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeParamListNode;

public class ClassImpl extends ReferentialTypeImpl implements IClass{

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
	protected IClass superClass;
	
	//TODO: Factor out? After all it isn't even used. maybe delete.
	private HashMap<String,IInterface> interfacesTransClosure;
	
	protected int classIndex;
	protected int minInstanceofIndex;
	
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
	
	public ClassImpl(ClassDeclNode declaration, IScope scope) {
		super(declaration, scope);
		this.nameProvider = new IndexClassNameProvider(this);
		interfacesTransClosure = new HashMap<String,IInterface>();
		this.declaration = declaration;
		
		//XPilot: moved from GenericClass
		TypeParamListNode tl = declaration.getTypeParams();
		int size = tl == null ? 0 : tl.size();
		typeParams = new TypeParameter[size];
		for(int i=0;i<size;i++){
			typeParams[i] = new TypeParameter(this, tl.elementAt(i),i,null);
		}
	}
	
	
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
		if(RecordTypeImpl.allowedAnnotations != null)
			allowedAnnotations.addAll(RecordTypeImpl.allowedAnnotations);
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


	private String metaClassName;
	
	public void setInstanceLimit(int instanceLimit) {
		this.instanceLimit = instanceLimit;
	}
	
	public int getInstanceLimit() {
		return instanceLimit;
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



	@Override
	public String getDescription() {
		return "class";
	}
	
	@Override
	public boolean canConcatenateCastTo(IType toType) {
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

	//TODO: Check if this is used somewhere
//	void generateClassIndex(TypeProvider tp){
//		minInstanceofIndex = tp.getCurInstanceofIndex();
//		
//		for(IRecordType r: descendants){
//			((IClass)r).generateClassIndex(tp);
//		}
//		
//		classIndex = tp.getNextClassIndex();
//		
//	}
	
	public boolean isTopClass(){
		return superClass == null;
	}

	public IClass getTopClass() {
		if(superClass != null) return superClass.getTopClass();
		return this;
	}
	
	@Override
	public IType getGeneratedType() {
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

	public IClass getSuperClass() {
		return superClass;
	}
	
	

	
	@Override
	public boolean isInstanceof(IClass c){
		//XPilot: changed implementation here, as it wasn't working properly...
		//return classIndex >= c.minInstanceofIndex && classIndex <= c.classIndex;
		if(this == c) return true;
		if(superClass == null) return false;
		return superClass.isInstanceof(c);
	}
	
	@Override
	public boolean canImplicitCastTo(IType toType) {
		if(toType==this) return true;
		//XPilot: Can now implicit cast to a generic type
		if(!toType.isClass()) return false;
		return (this.isInstanceof((IClass)toType));
	}
	
	@Override
	public boolean canExplicitCastTo(IType toType) {
		if(toType==this) return true;
		if(toType.isTypeOrExtension(BasicType.INT)) return true;
		if(!toType.isClass()) return false;
		return ((IClass)toType).isInstanceof(this)||(this.isInstanceof((IClass)toType));
	}

	public void registerInstantiation() {
		instantiationCount++;
		if(superClass != null) superClass.registerIndirectInstantiation();
	}

	public void registerIndirectInstantiation() {
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

	
	
	@Override
	public int calcByteSize() {
		//FIXME: Class field byte size calculation
		int result = 0;
//		for(VarDecl f: getTopClass().hierarchyFields){
//			result += f.getType().getMemberByteSize();
//		}
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


	public void setSuperClass(IClass type) {
		superClass = type;
	}


	public boolean hasConstructors() {
		return !constructors.isEmpty();
	}



	@Override
	public INamedType createGenericInstance(Signature s) {
		return new GenericClassInstance(this, s);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}