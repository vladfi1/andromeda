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

import java.security.AllPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.Destructor;
import com.sc2mod.andromeda.environment.IAnnotatable;
import com.sc2mod.andromeda.environment.IGlobal;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.MethodSet;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.StaticInit;
import com.sc2mod.andromeda.environment.Util;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclaration;
import com.sc2mod.andromeda.syntaxNodes.Annotation;
import com.sc2mod.andromeda.syntaxNodes.AnnotationList;
import com.sc2mod.andromeda.syntaxNodes.ClassBody;
import com.sc2mod.andromeda.syntaxNodes.ClassMemberDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ClassMemberType;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructure;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.TypeList;
import com.sc2mod.andromeda.syntaxNodes.TypeParam;

/**
 * A class or interface.
 * @author J. 'gex' Finis
 */
public abstract class RecordType extends SimpleType implements IModifiable, IGlobal, IAnnotatable {

	private String name;
	protected GlobalStructure declaration;
	
	public GlobalStructure getDeclaration() {
		return declaration;
	}

	//Hierarchy for topologic sorting and stuff
	protected LinkedList<RecordType> descendants;



	private boolean isAbstract;
	private boolean isFinal;
	private int visibility;
	
	//Members
	protected MethodSet methods;
	protected FieldSet fields;

	protected Destructor destructor;
	protected LinkedHashMap<Signature,Constructor> constructors;
	protected ArrayList<StaticInit> staticInits;
	
	private boolean typesResolved;
	protected boolean hierarchyChecked;
	private Scope scope;
	private int numStatics;
	private int numNonStatics;
	protected boolean membersResolved;
	private HashMap<String, Annotation> annotationTable;
	private int byteSize = -1;
	
	@Override
	public int getRuntimeType() {
		return RuntimeType.RECORD;
	}
	
	@Override
	public String getDefaultValueStr() {
		return "0";
	}
	
	
	protected static HashSet<String> allowedAnnotations = new HashSet<String>();
	//static{}
	
	@Override
	public HashSet<String> getAllowedAnnotations() {
		return allowedAnnotations;
	}

	@Override
	public boolean hasAnnotation(String name) {
		return annotationTable.containsKey(name);
	}
	
	@Override
	public void setAnnotationTable(HashMap<String, Annotation> annotations) {
		annotationTable = annotations;
	}
	
	
	public int getNumNonStatics() {
		return numNonStatics;
	}

	public int getNumStatics() {
		return numStatics;
	}

	public LinkedList<RecordType> getDecendants() {
		return descendants;
	}


	public MethodSet getMethods() {
		return methods;
	}

	public FieldSet getFields() {
		return fields;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract() {
		this.isAbstract = true;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal() {
		this.isFinal = true;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int newVisibility) {
		visibility = newVisibility;
	}
	
	@Override
	public boolean isStatic() {
		return false;
	}
	
	@Override
	public void setStatic() {
		throw new CompilationError(declaration.getModifiers(),"Record types cannot be 'static'.");
	}
	
	@Override
	public void setConst() {
		throw new CompilationError(declaration.getModifiers(),"Record types cannot be 'const'.");
	}
	
	@Override
	public boolean isConst() {
		return false;
	}
	
	public boolean isGeneric(){
		return false;
	}
	
	public TypeParameter[] getTypeParams(){
		throw new Error("Trying to call getTypeParams for record type!");
	}
	
	public GenericClass getGenericInstance(Signature s){
		throw new Error("Trying to call getGenericInstance for record type!");
	}
	
	public boolean resolveInheritance(TypeProvider t){
		if(typesResolved){
			return false;
		}
		typesResolved = true;
		return true;
	}
	
	@Override
	public String getFullName() {
		return getName();
	}
	

	public RecordType(GlobalStructure g, Scope s) {
		super();
		createMembers();
		this.declaration = g;
		//Ugly but necessary :(
		if(!(g instanceof EnrichDeclaration))
			this.name = g.getName();
		this.scope = s;
		g.setSemantics(this);
		Util.processModifiers(this,g.getModifiers());
		Util.processAnnotations(this, g.getAnnotations());
	}
	
	protected RecordType(RecordType genericParent){
		
	}
	
	private void createMembers() {
		descendants = new LinkedList<RecordType>();
		methods = new MethodSet(this);
		fields = new FieldSet();

		constructors = new LinkedHashMap<Signature, Constructor>();
		staticInits = new ArrayList<StaticInit>();
		
	}


	public String getName() {
		return name;
	}
	
	@Override
	public String getUid() {
		return getName();
	}

	public abstract void checkHierarchy(TypeProvider typeProvider);
	protected abstract void checkForHierarchyCircle(TypeProvider typeProvider,HashSet<RecordType> marked);
	protected static void doHierarchyCheck(RecordType t,HashSet<RecordType> marked){
		t.hierarchyChecked = true;
		if(marked.contains(t)){
			throw new CompilationError(t.declaration, "Cycle in inheritance hierarchy! Cycle contains type " + t.getName());
		}
		marked.add(t);
	}
	
	void resolveMembers(TypeProvider t){
		
		//Add members from super classes and interfaces, this should be done in the subtypes which then
		//call this method		
		
	
		
		
		//Add fields to counts
		numNonStatics += fields.numNonStaticFields();
		numStatics += fields.numStaticFields();
		
		//Resolve signatures and return types of functions and types of fields
		ClassBody body = declaration.getBody();
		int size = declaration.getBody().size();
		for(int i=0;i<size;i++){
			ClassMemberDeclaration c = body.elementAt(i);
			IModifiable member;
			switch(c.getMemberType()){
			case ClassMemberType.METHOD_DECLARATION:
				MethodDeclaration m = (MethodDeclaration)c;
				Method meth = new Method(m,this,scope);
				meth.resolveTypes(t,null);
				methods.addMethod(meth);
				member = meth;
				break;
			case ClassMemberType.FIELD_DECLARATION:
				//Fields were already resolved in constant early resolve visitor
				continue;
			case ClassMemberType.CONSTRUCTOR_DECLARATION:
			{
				MethodDeclaration d = (MethodDeclaration)c;
				Constructor con = new Constructor(d,(Class)this,scope);
				con.resolveTypes(t, null);
				Constructor old = constructors.put(con.getSignature(), con);
				/*
				if(old!=null)
					throw new CompilationError(con.getDefinition(),old
							.getDefinition(),
							"Duplicate constructor!","First Definition");
				*/
				member = con;
				break;
			}
			case ClassMemberType.DESTRUCTOR_DECLARATION:
			{
				MethodDeclaration d = (MethodDeclaration)c;
				Destructor con = new Destructor(d,(Class)this,scope);
				con.resolveTypes(t, null);
				if(destructor != null){
					throw new CompilationError(con.getDefinition(),destructor
							.getDefinition(),
							"Duplicate destructor!","First Definition");
				}
				destructor = con;
				member = con;
				break;
			}	
			case ClassMemberType.STATIC_INIT:
				StaticInit s = new StaticInit((StaticInitDeclaration)c,scope);
				s.resolveTypes(t, null);
				staticInits.add(s);
				t.addStaticInit(s);
				numStatics++;
				continue;
			case ClassMemberType.ACCESSOR_DECLARATION:
				AccessorDeclaration a = (AccessorDeclaration)c;
				AccessorDecl ad = new AccessorDecl(a, this, scope);
				ad.resolveType(t);
				fields.addField(ad);
				member = ad;
				break;
			default:
				throw new CompilationError("Unknown class member type " + c.getMemberType());
			}
			if(member.isStatic()){
				numStatics++;
			} else {
				numNonStatics++;
			}
		}
		
		membersResolved = true;
	
	}
	
	public Destructor getDestructor() {
		return destructor;
	}

	@Override
	public Scope getScope() {
		return scope;
	}
	
	@Override
	public boolean isNative() {
		return false;
	}
	
	@Override
	public void setNative() {
		throw new CompilationError(declaration.getModifiers(),"Type definitions cannot be 'native'.");
    }
	
	@Override
	public boolean isOverride() {
		return false;
	}

	@Override
	public void setOverride() {
		throw new CompilationError(declaration.getModifiers(),"Type definitions cannot be 'override'.");
		
	}
	
	@Override
	public boolean canHaveFields() {
		return true;
	}

	public boolean isInstanceof(Class curClass) {
		return false;
	}
	
	
	public boolean hasConstructors(){
		return !constructors.isEmpty();
	}
	
	@Override
	public void afterAnnotationsProcessed() {}
	
	protected int calcByteSize(){
		throw new Error("Cannot calculate record type bytesize");
	}
	
	@Override
	public int getByteSize() {
		if(byteSize==-1){
			byteSize = calcByteSize();
		}
		return byteSize;
	}
}
