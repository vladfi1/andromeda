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

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.generic.GenericStructInstance;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;

public class StructImpl extends RecordTypeImpl implements IStruct{

	private StructDeclNode declaration;

	public StructImpl(StructDeclNode declaration, IScope scope, TypeProvider t) {
		super(declaration, scope,t);
		this.declaration = declaration;
	}
	
	@Override
	public StructDeclNode getDefinition() {
		return declaration;
	}

	@Override
	public String getDescription() {
		return "struct";
	}
	

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.STRUCT;
	}
	
	/**
	 * Structs cannot be passed as parameter or returned
	 */
	@Override
	public boolean isValidAsParameter() {
		return false;
	}
	
	@Override
	public int calcByteSize() {
		int result = 0;
		for(IScopedElement elem : getContent().viewValues()){
			//We can cast do var decl here since structs only contain fields.
			VarDecl f = (VarDecl) elem;
			result += f.getType().getMemberByteSize();
		}
		return result;
	}


	@Override
	public INamedType createGenericInstance(Signature s) {
		return new GenericStructInstance(this,s,tprov);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
