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

import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNameNode;

public abstract class FieldOrAccessorDecl extends NonParamDecl {

	private int visibility;
	private boolean isStatic;
	private RecordType containingType;
	
	public FieldOrAccessorDecl(MemberDeclNode f, RecordType containingType, VarDeclNode varDecl) {
		super(f.getFieldModifiers(),f.getType(),varDecl);
		this.containingType = containingType;
	}
	
	public FieldOrAccessorDecl(MemberDeclNode f, RecordType containingType, VarDeclNameNode varDeclId) {
		super(f.getFieldModifiers(),f.getType(),varDeclId);
		this.containingType = containingType;
	}
	
	public RecordType getContainingType(){
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
	public int getVisibility() {
		return visibility;
	}

	@Override
	public void setStatic() {
		isStatic = true;
	}
	@Override
	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

}
