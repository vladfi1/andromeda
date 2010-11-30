package com.sc2mod.andromeda.environment.types.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassNameProvider;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.content.MethodSet;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.generic.GenericClassInstance;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;

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
	
	//TODO: Factor out, this is heavily related with code generation.
	private ArrayList<Variable> hierarchyFields;
	
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

	private String metaClassName;

	
	public ClassImpl(ClassDeclNode declaration, IScope scope, Environment env) {
		super(declaration, scope, env);
		this.nameProvider = new IndexClassNameProvider(this);
		interfacesTransClosure = new HashMap<String,IInterface>();
		this.declaration = declaration;
	}
	
	
	@Override
	public ClassDeclNode getDefinition() {
		return declaration;
	}
	
	@Override
	public OperationSet getConstructors() {
		return constructors;
	}
	
	@Override
	public void addConstructor(Constructor c){
		constructors.add(c);
	}

	@Override
	public Destructor getDestructor() {
		return destructor;
	}


	@Override
	public void setDestructor(Destructor destructor) {
		if(this.destructor != null){
			throw Problem.ofType(ProblemId.DUPLICATE_DESTRUCTOR).at(this.destructor.getDefinition(),destructor.getDefinition())
						.raiseUnrecoverable();
		} else {
			this.destructor = destructor;
		}
	}
		
	//FIXME CLass static checks, since all classes are now considered static
//	@Override
//	public boolean isStatic() {
//		return isStatic;
//	}
	
	@Override
	public VirtualCallTable getVirtualCallTable() {
		return virtualCallTable;
	}

	@Override
	public void setVirtualCallTable(VirtualCallTable virtualCallTable) {
		this.virtualCallTable = virtualCallTable;
	}

	@Override
	public void setInstanceLimit(int instanceLimit) {
		this.instanceLimit = instanceLimit;
	}
	
	@Override
	public int getInstanceLimit() {
		return instanceLimit;
	}
	
	@Override
	public ArrayList<Variable> getHierarchyFields() {
		return hierarchyFields;
	}

	@Override
	public void setHierarchyFields(ArrayList<Variable> hierarchyFields) {
		this.hierarchyFields = hierarchyFields;
	}

	@Override
	public String getDescription() {
		return "class";
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.CLASS;
	}
	
	@Override
	public boolean isImplicitReferenceType() {
		return true;
	}

	@Override
	public boolean canHaveMethods() {
		return true;
	}
	
	@Override
	public boolean isTopType() {
		return superClass == null;
	}

	@Override
	public IClass getTopClass() {
		if(superClass != null) return superClass.getTopClass();
		return this;
	}
	
	@Override
	public IType getGeneratedType() {
		return tprov.BASIC.INT;
	}
	
	@Override
	public String getGeneratedName() {
		return nameProvider.getName();
	}
	
	@Override
	public String getGeneratedDefinitionName() {
		return nameProvider.getName();
	}

	@Override
	public ClassNameProvider getNameProvider(){
		return nameProvider;
	}
	
	@Override
	public void setGeneratedName(String generatedName) {
		nameProvider.setName(generatedName);
	}

	@Override
	public IClass getSuperClass() {
		return superClass;
	}
	
	@Override
	public IType getSuperType() {
		return superClass;
	}
	
	@Override
	public boolean isSubtypeOf(IType c){
		if(this == c) return true;
		switch(c.getCategory()){
		case CLASS:
			if(superClass == null) return false;
			return superClass.isSubtypeOf(c);
		case INTERFACE:
			throw new Error("Interface subtyping not implemented yet");
		default:
			return false;
		}
	}
	
	@Override
	public void registerInstantiation() {
		instantiationCount++;
		if(superClass != null) superClass.registerIndirectInstantiation();
	}

	@Override
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
	@Override
	public boolean isUsed() {
		return (instantiationCount + indirectInstantiationCount) > 0;
	}

	@Override
	public void setMetaClassName(String name) {
		metaClassName = name;
	}
	
	@Override
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
	@Override
	public void generateImplementsTransClosure() {
	}

	
	
	@Override
	public int calcByteSize() {
		//FIXME: Class field byte size calculation
		int result = 0;
//		for(Variable f: getTopClass().hierarchyFields){
//			result += f.getType().getMemberByteSize();
//		}
		return result;
	}
	
	@Override
	public int getMemberByteSize() {
		return 4; //Classes are implicit ints
	}


	@Override
	public void setSuperClass(IClass type) {
		superClass = type;
	}

	@Override
	public boolean hasConstructors() {
		return !constructors.isEmpty();
	}
	
	@Override
	public INamedType createGenericInstance(Signature s) {
		return new GenericClassInstance(this, s, tprov);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
