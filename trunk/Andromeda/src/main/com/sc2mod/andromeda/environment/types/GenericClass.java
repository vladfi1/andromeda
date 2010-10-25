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
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;

/**
 * A generic class.
 * @author J. 'gex' Finis
 */
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class GenericClass extends Class{

	protected HashMap<Signature,GenericClassInstance> genericInstances;

	private Signature sig;

	/**
	 * Standard constructor for creating the class itself
	 * @param declaration
	 * @param scope
	 */
	public GenericClass(ClassDeclNode declaration, Scope scope) {
		super(declaration, scope);
		genericInstances = new HashMap<Signature, GenericClassInstance>();
		sig = new Signature(typeParams);
	}
	
	protected GenericClass(GenericClass genericParent){
		super(genericParent);
	}

	@Override
	public boolean isGeneric() {
		return true;
	}
	
	@Override
	public TypeCategory getCategory() {
		return TypeCategory.GENERIC_CLASS;
	}
	
	@Override
	public String getFullName() {
		return new StringBuilder().append(getUid()).append("<").append(new Signature(typeParams).getFullName()).append(">").toString();
	}
	
	public GenericClass getGenericInstance(Signature s){
		if(s.equals(sig)) return this;
		GenericClassInstance g = genericInstances.get(s);
		if(g == null){
			g = new GenericClassInstance(this,s);
		}
		return g;
	}

	@Override
	public Type getWrappedType() {
		return this;
	}

	@Override
	void resolveMembers(TypeProvider t) {		
		//Push generic types onto the stack
		t.pushTypeParams(typeParams);
		
		//Resolve members
		super.resolveMembers(t);
		
		//Pop generic type parameters from the stack
		t.popTypeParams(typeParams);
		
		//Generate generic members (i.e. copy members from this class and change type params
		// to the instanced ones) for all instances that are already present
		for(Entry<Signature, GenericClassInstance> e : genericInstances.entrySet()){
			e.getValue().generateGenericMembers();
		}
	}
	
	@Override
	public boolean containsTypeParams() {
		return true;
	}

	public Signature getSignature() {
		return sig;
	}
	
	@Override
	public Type replaceTypeParameters(TypeParamMapping paramMap) {
		if(!containsTypeParams()) return this;
		return getGenericInstance(sig.replaceTypeParameters(paramMap));
	}
	
	/**
	 * XPilot: Added for debugging.
	 */
	public boolean isGenericInstance() {
		return false;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}