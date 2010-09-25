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

import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class FieldDecl extends FieldOrAccessorDecl {

	private FieldDeclaration fieldDeclaration;
	private final int index;
	private int fieldIndex;
	private FieldDecl usesField;
	private ArrayList<FieldDecl> usedByFields;
	
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

	public FieldDecl(FieldDeclaration f, RecordType containingType, int index) {
		super(f,containingType,f.getDeclaredVariables().elementAt(index));
		this.fieldDeclaration = f;
		this.index = GlobalVarDecl.curIndex++;		

	}	

	@Override
	public SyntaxNode getDefinition() {
		return fieldDeclaration;
	}
	public FieldDeclaration getFieldDeclaration(){
		return fieldDeclaration;
	}
	

	@Override
	public int getDeclType() {
		return isStatic()?TYPE_STATIC_FIELD:TYPE_FIELD;
	}
	
	public boolean isGlobalField(){
		return isStatic();
	}

	@Override
	public boolean isAccessor() {
		return false;
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

}
