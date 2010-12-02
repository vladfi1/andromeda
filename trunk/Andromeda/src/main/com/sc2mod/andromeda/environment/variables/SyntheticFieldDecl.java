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

import com.sc2mod.andromeda.codegen.buffers.AdvancedBuffer;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;

public class SyntheticFieldDecl extends VarDecl{


	public SyntheticFieldDecl(ModifierListNode mods, IType type,
			IdentifierNode def, IScope scope) {
		super(mods, type, def, scope);
	}

	@Override
	public boolean isInitedInDecl() {
		return false;
	}
	//TODO make class and this method abstract (then the visitor generator has to be fixed)
	public void genCode(AdvancedBuffer codeGenerator, ExprNode node){}
		
		
	@Override
	public String getDescription() {
		return "synthetic field " + getUid();
	}
	
	@Override
	public VarType getVarType() {
		return VarType.SYNTHETIC_SPECIAL;
	}
	
	@Override
	public boolean isStaticElement() {
		return getModifiers().isStatic();
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
