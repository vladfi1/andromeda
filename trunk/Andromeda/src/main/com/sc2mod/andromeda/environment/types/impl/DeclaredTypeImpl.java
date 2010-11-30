package com.sc2mod.andromeda.environment.types.impl;

import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.ModifierSet;
import com.sc2mod.andromeda.environment.annotations.AnnotationSet;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

/**
 * A declared type is a named type that is explicitly declared
 * in the program and can have modifiers and annotations (in contrast to a basic type).
 * @author gex
 *
 */
public abstract class DeclaredTypeImpl extends NamedTypeImpl implements IDeclaredType{

	private GlobalStructureNode declaration;
	private AnnotationSet annotations;
	private final ModifierSet modifiers;
	
	//Hierarchy for topologic sorting and stuff
	protected LinkedList<IDeclaredType> descendants = new LinkedList<IDeclaredType>();
	
	
	
	protected DeclaredTypeImpl(GlobalStructureNode declaration, IScope parentScope, Environment env) {
		super(parentScope, declaration.getName(),env.typeProvider);
		this.declaration = declaration;
		declaration.setSemantics(this);
		modifiers = ModifierSet.create(this, declaration.getModifiers());
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
		
	@Override public boolean isStaticElement() { return true; }
	
	@Override
	public Visibility getVisibility() {
		return modifiers.getVisibility();
	}
	
	@Override
	public ModifierSet getModifiers() {
		return modifiers;
	}
	
	
		
	public TypeParameter[] getTypeParams(){
		throw new Error("Trying to call getTypeParams for record type!");
	}

}
