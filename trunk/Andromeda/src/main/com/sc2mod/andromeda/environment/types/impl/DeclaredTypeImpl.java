package com.sc2mod.andromeda.environment.types.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.ModifierUtil;
import com.sc2mod.andromeda.environment.annotations.AnnotationSet;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.IRecordType;
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
	private AnnotationSet annotations;
	//Hierarchy for topologic sorting and stuff
	protected LinkedList<IDeclaredType> descendants = new LinkedList<IDeclaredType>();
	
	
	
	protected DeclaredTypeImpl(GlobalStructureNode declaration, IScope parentScope, Environment env) {
		super(parentScope, declaration.getName(),env.typeProvider);
		this.declaration = declaration;
		declaration.setSemantics(this);
		
		ModifierUtil.processModifiers(this,declaration.getModifiers());
		env.annotationRegistry.processAnnotations(this, declaration.getAnnotations());
	}
	
	@Override
	public AnnotationSet getAnnotations(boolean createIfNotExistant) {
		if(annotations == null && createIfNotExistant){
			annotations = new AnnotationSet();
		}
		return annotations;
	}
	
	@Override
	public LinkedList<IDeclaredType> getDescendants() {
		return descendants;
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
		Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("Type definitions","native")
					.raise();
	}
	
	@Override public boolean isOverride() { return false; }
	@Override public void setOverride() {
		Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
		.details("Type definitions","override")
		.raise(); 
	}
		
	@Override public boolean isStatic() { return false; }
	@Override public void setStatic() {
		Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("This kind of type","static")
					.raise();
	}
	
	@Override public boolean isConst() { return false; }
	@Override public void setConst() {
		Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
			.details("Type definitions","const")
			.raise();
	}
	
	@Override public boolean isAbstract() { return false; }
	@Override public void setAbstract() {
		Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("This kind of type","abstract")
					.raise();
	}
	
	@Override public boolean isFinal() { return false; }
	@Override public void setFinal() {
		Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getModifiers())
					.details("This kind of type","final")
					.raise();
	}
	
	
	
	public TypeParameter[] getTypeParams(){
		throw new Error("Trying to call getTypeParams for record type!");
	}

}
