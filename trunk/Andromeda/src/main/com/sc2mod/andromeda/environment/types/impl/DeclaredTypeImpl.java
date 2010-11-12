package com.sc2mod.andromeda.environment.types.impl;

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.environment.StructureUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

/**
 * A declared type is a named type that is explicitly declared
 * in the program and can have modifiers and annotations (in contrast to a basic type).
 * @author gex
 *
 */
public abstract class DeclaredTypeImpl extends NamedTypeImpl implements IDeclaredType{

	private Visibility visibility = Visibility.DEFAULT;
	private GlobalStructureNode declaration;
	
	protected DeclaredTypeImpl(GlobalStructureNode declaration, IScope parentScope, TypeProvider t) {
		super(parentScope, declaration.getName(),t);
		this.declaration = declaration;
		declaration.setSemantics(this);
		
		StructureUtil.processModifiers(this,declaration.getModifiers());
		StructureUtil.processAnnotations(this, declaration.getAnnotations());
	}
	
	@Override
	public GlobalStructureNode getDefinition() {
		return declaration;
	}
	
	@Override
	public Visibility getVisibility() {
		return visibility;
	}

	@Override
	public void setVisibility(Visibility newVisibility) {
		visibility = newVisibility;
	}
	
	@Override public boolean isNative() { return false; }
	@Override public void setNative() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("Type definitions","native")
					.raiseUnrecoverable();
	}
	
	@Override public boolean isOverride() { return false; }
	@Override public void setOverride() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
		.details("Type definitions","override")
		.raiseUnrecoverable(); 
	}
		
	@Override public boolean isStatic() { return false; }
	@Override public void setStatic() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("This kind of type","static")
					.raiseUnrecoverable();
	}
	
	@Override public boolean isConst() { return false; }
	@Override public void setConst() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
			.details("Type definitions","const")
			.raiseUnrecoverable();
	}
	
	@Override public boolean isAbstract() { return false; }
	@Override public void setAbstract() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("This kind of type","abstract")
					.raiseUnrecoverable();
	}
	
	@Override public boolean isFinal() { return false; }
	@Override public void setFinal() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("This kind of type","final")
					.raiseUnrecoverable();
	}
	
	
	
	public TypeParameter[] getTypeParams(){
		throw new Error("Trying to call getTypeParams for record type!");
	}
	
	//FIXME: Rework annotations

	private HashMap<String, AnnotationNode> annotations;
	@Override
	public void afterAnnotationsProcessed() {}
	
	@Override
	public HashSet<String> getAllowedAnnotations() {
	return null;
	}
	
	@Override
	public void setAnnotationTable(HashMap<String, AnnotationNode> annotations) {
	}
	
	@Override
	public boolean hasAnnotation(String name) {
		return false;
	}
	

}
