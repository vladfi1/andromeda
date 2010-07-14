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
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.TypeParamList;

public class GenericClass extends Class{

	protected HashMap<Signature,GenericClassInstance> genericInstances;

	protected TypeParameter[] typeParams;
	private Signature sig;
	
	public TypeParameter[] getTypeParams(){
		return typeParams;
	}
	
	/**
	 * Standard constructor for creating the class itself
	 * @param declaration
	 * @param scope
	 */
	public GenericClass(ClassDeclaration declaration, Scope scope) {
		super(declaration, scope);
		TypeParamList tl = declaration.getTypeParams();
		int size = tl.size();
		typeParams = new TypeParameter[size];
		genericInstances = new HashMap<Signature, GenericClassInstance>();
		for(int i=0;i<size;i++){
			typeParams[i] = new TypeParameter(this, tl.elementAt(i));
		}
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
	public int getCategory() {
		return GENERIC_CLASS;
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
		
		//Pop generic type paramers from the stakc
		t.popTypeParams(typeParams);
		
		//Generate generic members (i.e. copy members from this class and change type params
		// to the instanced onces) for all instances that are already present
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
}
