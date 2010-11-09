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

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

public class FieldDecl extends NonLocalVarDecl {

	private FieldDeclNode fieldDeclaration;
	private final int index;
	private int fieldIndex;
	private FieldDecl usesField;
	private ArrayList<FieldDecl> usedByFields;
	private boolean isStatic;
	private IType containingType;
	
	public FieldDecl(FieldDeclNode f, VarDeclNode declNode, IType containingType, IScope scope) {
		super(f,declNode,scope);
		this.fieldDeclaration = f;
		this.index = GlobalVarDecl.curIndex++;	
		this.containingType = containingType;

	}	
	
	public ArrayList<FieldDecl> getUsedByFields() {
		return usedByFields;
	}
	
	public FieldDecl getUsesField() {
		return usesField;
	}

	public void setUsesField(FieldDecl usesField) {
		this.usesField = usesField;
		if(usesField.usedByFields == null){
			usesField.usedByFields = new ArrayList<FieldDecl>(3);
		}
		usesField.usedByFields.add(this);
	}
	
	@Override
	public String getGeneratedName() {
		//XPilot: usesField seems to always be null...
		if(usesField!=null) return usesField.getGeneratedName();
		return super.getGeneratedName();
	}

	public FieldDeclNode getFieldDeclaration(){
		return fieldDeclaration;
	}
	
	
	public boolean isGlobalField(){
		return isStatic();
	}
	
	@Override
	public int getIndex() {
		return index;
	}

	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}

	public int getFieldIndex() {
		return fieldIndex;
	}
	
	public IType getContainingType(){
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
	



	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }

	@Override
	public VarType getVarType() {
		return isStatic ? VarType.STATIC : VarType.FIELD ;
	}
}
