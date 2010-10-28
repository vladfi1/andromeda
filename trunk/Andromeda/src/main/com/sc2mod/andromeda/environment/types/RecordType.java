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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.IAnnotatable;
import com.sc2mod.andromeda.environment.IGlobal;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Util;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

/**
 * A class or interface.
 * @author J. 'gex' Finis
 */
public abstract class RecordType extends NamedType implements IModifiable, IGlobal, IAnnotatable {

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
	
	/**
	 * Constructor for generic instances of a type.
	 * @param genericParent the type for which to create a generic instance.
	 */
	protected RecordType(RecordType genericParent){
		super(GENERIC,genericParent);
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
