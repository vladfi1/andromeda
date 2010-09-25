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
import com.sc2mod.andromeda.syntaxNodes.BlockStatement;
import com.sc2mod.andromeda.syntaxNodes.ClassMemberType;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.MethodHeader;
import com.sc2mod.andromeda.syntaxNodes.StatementList;

public class Deallocator extends Destructor{

	private Deallocator(MethodDeclaration functionDeclaration, Class clazz,
			Scope scope) {
		super(functionDeclaration, clazz, scope);
		this.setReturnType(SpecialType.VOID);
		setLocals(new LocalVarDecl[0]);

	}
	
	public static Deallocator createDeallocator(TypeProvider tp,Class clazz){
		MethodHeader mh = new MethodHeader(null,null, null, clazz.getName(), null, null);
		MethodDeclaration md = new MethodDeclaration(ClassMemberType.DESTRUCTOR_DECLARATION, mh, new BlockStatement(new StatementList()));
		Deallocator da = new Deallocator(md, clazz, clazz.getScope());
		da.resolveTypes(tp, null);
		return da;
	}


}
