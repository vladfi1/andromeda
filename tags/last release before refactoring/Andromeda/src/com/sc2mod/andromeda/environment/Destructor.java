/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.LinkedList;
import java.util.List;

import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclaratorId;

public class Destructor extends Method {

	private ConstructorInvocation invokedConstructor;
	
		
	public ConstructorInvocation getInvokedConstructor() {
		return invokedConstructor;
	}

	public void setInvokedConstructor(ConstructorInvocation invokedConstructor) {
		this.invokedConstructor = invokedConstructor;
	}

	public Destructor(MethodDeclaration functionDeclaration, Class clazz, Scope scope) {
		super(functionDeclaration,clazz, scope);
	}
	
	@Override
	public void resolveTypes(TypeProvider t, List<ParamDecl> implicitParameters) {
		RecordType container = getContainingType();
		//Is this really a constructor or just a method without return type
		if(!declaration.getHeader().getName().equals(container.getName())){
			throw new CompilationError(declaration,"Destructors must have the name of their class.");
		}
		
		this.setReturnType(SpecialType.VOID);
		
		super.resolveTypes(t, implicitParameters);
	}
	
	@Override
	public void setAbstract() {
		throw new CompilationError(declaration.getHeader().getModifiers(),"Destructors cannot be 'abstract'.");
	}
	
	@Override
	public void setStatic() {
		throw new CompilationError(declaration.getHeader().getModifiers(),"Destructors cannot be 'static'.");
	}
	
	
	/**
	 * Destructors use this
	 */
	@Override
	public boolean usesThis() {
		return true;
	}
	
	public int getFunctionType(){
		return TYPE_DESTRUCTOR;
	}
	
	@Override
	public boolean isMember() {
		return true;
	}
}
