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
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNameNode;

public class Constructor extends Method {

	private ConstructorInvocation invokedConstructor;
	
	public static final List<ParamDecl> IMPLICIT_CONSTRUCTOR_PARAMS = new LinkedList<ParamDecl>();
	
	static{
		IMPLICIT_CONSTRUCTOR_PARAMS.add(new ParamDecl(null, BasicType.INT, new VarDeclNameNode("A__cid", null)));
	}
	
	public ConstructorInvocation getInvokedConstructor() {
		return invokedConstructor;
	}

	public void setInvokedConstructor(ConstructorInvocation invokedConstructor) {
		this.invokedConstructor = invokedConstructor;
	}

	public Constructor(MethodDeclNode functionDeclaration, Class clazz, Scope scope) {
		super(functionDeclaration,clazz, scope);
	}
	
	@Override
	public void resolveTypes(TypeProvider t, List<ParamDecl> implicitParameters) {
		RecordType container = getContainingType();
		//Is this really a constructor or just a method without return type
		if(!declaration.getHeader().getName().equals(container.getName())){
			throw Problem.ofType(ProblemId.MISSING_RETURN_TYPE).at(declaration)
					.raiseUnrecoverable();
		}
		
		this.setReturnType(SpecialType.VOID);
		
		super.resolveTypes(t, implicitParameters);
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

	/**
	 * Constructors use this, but not as parameter.
	 */
	@Override
	public boolean usesThis() {
		return false;
	}
	
	public int getFunctionType(){
		return TYPE_CONSTRUCTOR;
	}

	@Override
	public boolean isMember() {
		return true;
	}
}
