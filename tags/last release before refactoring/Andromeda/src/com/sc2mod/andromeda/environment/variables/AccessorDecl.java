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

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclaration;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclaratorId;

public class AccessorDecl extends FieldOrAccessorDecl{

	private AccessorDeclaration declaration;
	private Method getter;
	public synchronized Method getGetter() {
		return getter;
	}

	public synchronized Method getSetter() {
		return setter;
	}

	private Method setter;
	
	public AccessorDecl(AccessorDeclaration a, RecordType containingType, Scope scope) {
		super(a,containingType,a.getAccessorName());
		a.setSemantics(this);
		this.declaration = a;
		String name = a.getAccessorName().getName();
		MethodDeclaration m;
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
	public void resolveType(TypeProvider t) {
		super.resolveType(t);
		if(getter!=null)getter.resolveTypes(t,null);
		if(setter!=null){
			List<ParamDecl> param = new ArrayList<ParamDecl>(1);
			param.add(new ParamDecl(null, this.getType(), new VariableDeclaratorId("value",null)));
			setter.resolveTypes(t,param);
		}
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
	
}
