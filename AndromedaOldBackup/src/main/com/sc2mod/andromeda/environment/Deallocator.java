/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.List;

import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MemberTypeSE;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodHeaderNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;

public class Deallocator extends Destructor{

	private Deallocator(MethodDeclNode functionDeclaration, Class clazz,
			Scope scope) {
		super(functionDeclaration, clazz, scope);
		this.setReturnType(SpecialType.VOID);
		setLocals(new LocalVarDecl[0]);

	}
	
	public static Deallocator createDeallocator(TypeProvider tp,Class clazz){
		MethodHeaderNode mh = new MethodHeaderNode(null,null, null, clazz.getName(), null, null);
		MethodDeclNode md = new MethodDeclNode(MemberTypeSE.DESTRUCTOR_DECLARATION, mh, new BlockStmtNode(new StmtListNode()));
		Deallocator da = new Deallocator(md, clazz, clazz.getScope());
		da.resolveTypes(tp, null);
		return da;
	}


}
