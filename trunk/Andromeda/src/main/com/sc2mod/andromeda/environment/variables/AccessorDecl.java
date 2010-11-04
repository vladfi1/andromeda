/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.scopes.AccessType;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class AccessorDecl extends FieldOrAccessorDecl{

	private AccessorDeclNode declaration;
	private Method getter;
	public synchronized Method getGetter() {
		return getter;
	}

	public synchronized Method getSetter() {
		return setter;
	}

	private Method setter;
	
	public AccessorDecl(AccessorDeclNode a, IType containingType, IScope scope) {
		super(a,containingType,a.getAccessorName(), scope);
		a.setSemantics(this);
		this.declaration = a;
		String name = a.getAccessorName().getId();
		MethodDeclNode m;
		m = a.getGetMethod();
		if(m != null){
			getter = new Method(m, containingType, scope);
			m.getHeader().setReturnType(a.getType());
			getter.setName("get__".concat(name));
		}
		
		m = a.getSetMethod();
		if(m != null){
			setter = new Method(m, containingType, scope);
			//Setter has return type void
			setter.setName("set__".concat(name));
		}
	}	
	
	@Override
	public int getDeclType() {
		return isStatic()?TYPE_STATIC_ACCESSOR:TYPE_ACCESSOR;
	}


	@Override
	public boolean isAccessor() {
		return true;
	}
	
	@Override
	public SyntaxNode getDefinition() {
		return declaration;
	}
	
	@Override
	public boolean isInitDecl() {
		return false;
	}
	

	public void doAccessChecks(IScope from, AccessType accessType,
			SyntaxNode where) {
		
		switch(accessType){
		case LVALUE:
			checkSetter(from,where);
			break;
		case RVALUE:
			checkGetter(from,where);
			break;
		case LRVALUE:
			checkSetter(from,where);
			checkGetter(from,where);
			break;
		default:
			throw ErrorUtil.illegalSwitchValue(accessType);
		}
	}
	
	private void check(Method toCheck, IScope from, SyntaxNode where,String accessName, ProblemId onlyProblem ){
		if(toCheck == null){
			throw Problem.ofType(onlyProblem).at(where)
				.details(getUid())
				.raiseUnrecoverable();
		}
		if(!toCheck.getVisibility().checkAccessible(from,getScope())){
			throw Problem.ofType(ProblemId.ACCESSOR_NOT_VISIBLE).at(where)
					.details(getUid(),accessName)
					.raiseUnrecoverable();
		}
	}

	private void checkGetter(IScope from, SyntaxNode where) {
		check(getter,from, where, "read",ProblemId.ACCESSOR_WRITE_ONLY);
	}

	private void checkSetter(IScope from, SyntaxNode where) {
		check(setter,from, where, "write",ProblemId.ACCESSOR_WRITE_ONLY);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
