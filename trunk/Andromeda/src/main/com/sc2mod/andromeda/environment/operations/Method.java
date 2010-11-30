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
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;

public class Method extends Function {

	private IType containingType;
	
	public Method( MethodDeclNode functionDeclaration, IType containingType, IScope scope, Environment env) {
		super(functionDeclaration,scope,env);
		this.containingType = containingType;
	}

	@Override
	protected OverrideInformation createOverrideInformation() {
		return new MethodOverrideInformation(this);
	}
		
	@Override
	public String getDescription() {
		IType t = getContainingType();
		if(t == null) return "method " + getUid();
		return "method " + t.getFullName() + "." + getUid();
	}
	
	@Override
	public OperationType getOperationType() {
		return modifiers.isStatic()?OperationType.STATIC_METHOD:OperationType.METHOD;
	}
	
	@Override
	public IType getContainingType() {
		return containingType;
	}
	
	@Override
	public String getElementTypeName() {
		return "method";
	}

	@Override public boolean isStaticElement() { return modifiers.isStatic(); }

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
