/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types.impl;

import java.util.ArrayList;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;

public class InterfaceImpl extends ReferentialTypeImpl implements IInterface {

	private InterfaceDeclNode declaration;

	
	
	private int index;
	private int tableIndex;
	private ArrayList<IClass> implementingClasses = new ArrayList<IClass>();
	
	public InterfaceImpl(InterfaceDeclNode declaration, IScope scope) {
		super(declaration,scope);
		super.setAbstract();
		this.declaration = declaration;
	}
	
	/**
	 * The index is used to locate the bit for
	 * instanceof with this interface
	 * @return index
	 */
	public int getIndex() {
		return index;
	}
	
	@Override
	public InterfaceDeclNode getDefinition() {
		return declaration;
	}

	/**
	 * The table index is used to locate the slot in the interface
	 * table.
	 * @return table index
	 */
	public int getTableIndex() {
		return tableIndex;
	}



	@Override
	public String getDescription() {
		return "interface";
	}


	
	@Override
	public TypeCategory getCategory() {
		return TypeCategory.INTERFACE;
	}
	
	@Override
	public void setAbstract() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(this.declaration.getModifiers())
					.details("Interfaces","abstract")
					.raiseUnrecoverable();
	}
	
	@Override
	public boolean canHaveFields() {
		return false;
	}
	
	@Override
	public boolean isImplicitReferenceType() {
		return true;
	}
	
	@Override
	public boolean canHaveMethods() {
		return true;
	}
	
	@Override
	public int getByteSize() {
		throw new Error("Getting byte size of interface impossible!");
	}


	@Override
	public INamedType createGenericInstance(Signature s) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
