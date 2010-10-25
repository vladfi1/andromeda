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

import java.util.HashSet;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class Struct extends RecordType {

	private StructDeclNode declaration;

	public Struct(StructDeclNode declaration, Scope scope) {
		super(declaration, scope);
		this.declaration = declaration;
	}
	
	@Override
	public StructDeclNode getDefinition() {
		return declaration;
	}

	@Override
	public String getDescription() {
		return "Struct, defined at:\n" + CompilationFileManager.getLastEnvironment().getSourceInformation(declaration);
	}
	

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.STRUCT;
	}
	
	
	
	@Override
	void resolveMembers(TypeProvider t) {
		super.resolveMembers(t);
		for(String name: fields.getFieldNames()){
			//Since the parser forbids accessors in structs, we can safely cast to fieldDecl here
			FieldDecl field = (FieldDecl)fields.getFieldByName(name);
			if(!field.getFieldDeclaration().getFieldModifiers().isEmpty()){
				throw Problem.ofType(ProblemId.STRUCT_MEMBER_WITH_MODIFIER).at(field.getFieldDeclaration().getFieldModifiers())
								.raiseUnrecoverable();
			}
		}
	}
	


	/**
	 * Structs cannot be passed as parameter or returned
	 */
	@Override
	public boolean isValidAsParameter() {
		return false;
	}
	
	@Override
	protected int calcByteSize() {
		int result = 0;
		for(String s: fields.getFieldNames()){
			VarDecl f = fields.getFieldByName(s);
			result += f.getType().getMemberByteSize();
		}
		return result;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
