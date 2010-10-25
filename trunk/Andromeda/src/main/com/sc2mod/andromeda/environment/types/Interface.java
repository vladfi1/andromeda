/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;

public class Interface extends ReferentialType implements SemanticsVisitorNode {

	private InterfaceDeclNode declaration;

	
	
	private int index;
	private int tableIndex;
	private ArrayList<Class> implementingClasses = new ArrayList<Class>();
	
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

	public Interface(InterfaceDeclNode declaration, Scope scope) {
		super(declaration,scope);
		super.setAbstract();
		this.declaration = declaration;
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
	
	void generateInterfaceIndex(TypeProvider tp){
		
		index = tp.getNextInterfaceIndex();
		
	}
	
	@Override
	public int getByteSize() {
		throw new Error("Getting byte size of interface impossible!");
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
