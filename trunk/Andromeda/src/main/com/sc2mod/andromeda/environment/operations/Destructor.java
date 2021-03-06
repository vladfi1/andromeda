/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.operations;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.access.ConstructorInvocation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;

public class Destructor extends Method {

	private ConstructorInvocation invokedConstructor;
	
		
	public ConstructorInvocation getInvokedConstructor() {
		return invokedConstructor;
	}

	public void setInvokedConstructor(ConstructorInvocation invokedConstructor) {
		this.invokedConstructor = invokedConstructor;
	}

	public Destructor(MethodDeclNode functionDeclaration, IClass clazz, IScope scope, Environment env) {
		super(functionDeclaration,clazz, scope, env);
	}
		
	public OperationType getOperationType(){
		return OperationType.DESTRUCTOR;
	}
	
	@Override
	public String getElementTypeName() {
		return "destructor";
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
