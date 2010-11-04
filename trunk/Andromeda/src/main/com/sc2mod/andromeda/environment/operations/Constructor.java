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

import java.util.LinkedList;
import java.util.List;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class Constructor extends Method {

	private ConstructorInvocation invokedConstructor;
	
	public static final List<ParamDecl> IMPLICIT_CONSTRUCTOR_PARAMS = new LinkedList<ParamDecl>();
	
	static{
		IMPLICIT_CONSTRUCTOR_PARAMS.add(new ParamDecl(null, BasicType.INT, new IdentifierNode("A__cid")));
	}
	
	public ConstructorInvocation getInvokedConstructor() {
		return invokedConstructor;
	}

	public void setInvokedConstructor(ConstructorInvocation invokedConstructor) {
		this.invokedConstructor = invokedConstructor;
	}

	public Constructor(MethodDeclNode functionDeclaration, IClass clazz, IScope scope) {
		super(functionDeclaration,clazz, scope);
	}
	
	@Override
	public void setAbstract() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getHeader().getModifiers())
					.details("Constructors","abstract")
					.raiseUnrecoverable();
	}
	
	@Override
	public void setStatic() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getHeader().getModifiers())
					.details("Constructors","static")
					.raiseUnrecoverable();
	}
	
	public OperationType getOperationType(){
		return OperationType.CONSTRUCTOR;
	}
	
	@Override
	public String getElementTypeName() {
		return "constructor";
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
