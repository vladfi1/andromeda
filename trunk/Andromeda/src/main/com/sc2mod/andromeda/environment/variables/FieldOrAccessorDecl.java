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

import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

public abstract class FieldOrAccessorDecl extends NonParamDecl {


	private boolean isStatic;
	private Type containingType;
	
	public FieldOrAccessorDecl(MemberDeclNode f, Type containingType, VarDeclNode varDecl, Scope scope) {
		super(f.getFieldModifiers(),f.getType(),varDecl, scope);
		this.containingType = containingType;
		
	}
	
	public FieldOrAccessorDecl(MemberDeclNode f, Type containingType, IdentifierNode varDeclId, Scope scope) {
		super(f.getFieldModifiers(),f.getType(),varDeclId, scope);
		this.containingType = containingType;
	}


	public Type getContainingType(){
		return containingType;
	}
	
	@Override
	public boolean isMember() {
		return !isStatic;
	}
	
	@Override
	public boolean isStatic() {
		return isStatic;
	}
	
	@Override
	public void setStatic() {
		isStatic = true;
	}
	

	@Override
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	


}
