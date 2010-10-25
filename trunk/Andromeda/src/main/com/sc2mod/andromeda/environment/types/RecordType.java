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
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.IAnnotatable;
import com.sc2mod.andromeda.environment.IGlobal;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.MethodSet;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Util;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclNode;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * A class or interface.
 * @author J. 'gex' Finis
 */
public abstract class RecordType extends SimpleType implements IModifiable, IGlobal, IAnnotatable {

	private String name;
	protected GlobalStructureNode declaration;
	
	@Override
	public abstract GlobalStructureNode getDefinition();

	//Hierarchy for topologic sorting and stuff
	protected LinkedList<RecordType> descendants;

	public LinkedList<RecordType> getDescendants() {
		return descendants;
	}

	private boolean isAbstract;
	private boolean isFinal;
	private Visibility visibility = Visibility.DEFAULT;
	
	//Members
	protected boolean hierarchyChecked;
	private Scope scope;
	private int numStatics;
	private int numNonStatics;
	protected boolean membersResolved;
	private HashMap<String, AnnotationNode> annotationTable;
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
	public void setAnnotationTable(HashMap<String, AnnotationNode> annotations) {
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

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility newVisibility) {
		visibility = newVisibility;
	}
	
	@Override
	public boolean isStatic() {
		return false;
	}
	
	@Override
	public void setStatic() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("Record types","static")
					.raiseUnrecoverable();
	}
	
	@Override
	public void setConst() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
			.details("Record types","const")
			.raiseUnrecoverable();
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

	@Override
	public String getFullName() {
		return getName();
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

	public RecordType(GlobalStructureNode g, Scope s) {
		super(s);
		createMembers();
		this.declaration = g;
		//Ugly but necessary :(
		if(!(g instanceof EnrichDeclNode))
			this.name = g.getName();
		this.scope = s;
		g.setSemantics(this);
		Util.processModifiers(this,g.getModifiers());
		Util.processAnnotations(this, g.getAnnotations());
	}
	
	protected RecordType(RecordType genericParent){
		super(genericParent);
	}
	
	private void createMembers() {
		descendants = new LinkedList<RecordType>();
		
	}


	public String getName() {
		return name;
	}
	
	@Override
	public String getUid() {
		return getName();
	}


//	
//	void resolveMembers(TypeProvider t){
//		
//		//Add members from super classes and interfaces, this should be done in the subtypes which then
//		//call this method		
//		
//	
//		
//		
//		//Add fields to counts
//		numNonStatics += fields.numNonStaticFields();
//		numStatics += fields.numStaticFields();
//		
//		//Resolve signatures and return types of functions and types of fields
//		MemberDeclListNode body = declaration.getBody();
//		int size = declaration.getBody().size();
//		for(int i=0;i<size;i++){
//			MemberDeclNode c = body.elementAt(i);
//			IModifiable member;
//			switch(c.getMemberType()){
//			case MemberTypeSE.METHOD_DECLARATION:
//				MethodDeclNode m = (MethodDeclNode)c;
//				Method meth = new Method(m,this,scope);
//				meth.resolveTypes(t,null);
//				methods.addMethod(meth);
//				member = meth;
//				break;
//			case MemberTypeSE.FIELD_DECLARATION:
//				//Fields were already resolved in constant early resolve visitor
//				continue;
//			case MemberTypeSE.CONSTRUCTOR_DECLARATION:
//			{
//				MethodDeclNode d = (MethodDeclNode)c;
//				Constructor con = new Constructor(d,(Class)this,scope);
//				con.resolveTypes(t, null);
//				Constructor old = constructors.put(con.getSignature(), con);
//				/*
//				if(old!=null)
//					throw new CompilationError(con.getDefinition(),old
//							.getDefinition(),
//							"Duplicate constructor!","First Definition");
//				*/
//				member = con;
//				break;
//			}
//			case MemberTypeSE.DESTRUCTOR_DECLARATION:
//			{
//				MethodDeclNode d = (MethodDeclNode)c;
//				Destructor con = new Destructor(d,(Class)this,scope);
//				con.resolveTypes(t, null);
//				if(destructor != null){
//					throw Problem.ofType(ProblemId.DUPLICATE_DESTRUCTOR).at(con.getDefinition(),destructor.getDefinition())
//								.raiseUnrecoverable();
//				}
//				destructor = con;
//				member = con;
//				break;
//			}	
//			case MemberTypeSE.STATIC_INIT:
//				StaticInit s = new StaticInit((StaticInitDeclNode)c,scope);
//				s.resolveTypes(t, null);
//				staticInits.add(s);
//				t.addStaticInit(s);
//				numStatics++;
//				continue;
//			case MemberTypeSE.ACCESSOR_DECLARATION:
//				AccessorDeclNode a = (AccessorDeclNode)c;
//				AccessorDecl ad = new AccessorDecl(a, this, scope);
//				ad.resolveType(t);
//				fields.addField(ad);
//				member = ad;
//				break;
//			default:
//				throw new InternalProgramError(c,"Unknown class member type " + c.getMemberType());
//			}
//			if(member.isStatic()){
//				numStatics++;
//			} else {
//				numNonStatics++;
//			}
//		}
//		
//		membersResolved = true;
//	
//	}

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
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("Type definitions","native")
					.raiseUnrecoverable();
	}
	
	@Override
	public boolean isOverride() {
		return false;
	}

	@Override
	public void setOverride() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
		.details("Type definitions","override")
		.raiseUnrecoverable();
	
	}
	
	@Override
	public boolean canHaveFields() {
		return true;
	}

	public boolean isInstanceof(Class curClass) {
		return false;
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
