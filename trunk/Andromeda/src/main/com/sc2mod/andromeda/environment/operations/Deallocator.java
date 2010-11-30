/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.operations;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodHeaderNode;
import com.sc2mod.andromeda.syntaxNodes.MethodTypeSE;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;

public class Deallocator extends Destructor{

	private Deallocator(MethodDeclNode functionDeclaration, IClass clazz,
			IScope scope, Environment env) {
		super(functionDeclaration, clazz, scope, env);
		this.setReturnType(clazz.getTypeProvider().BASIC.VOID);
		this.setResolvedParameters(new ParamDecl[0]);
		setLocals(new LocalVarDecl[0]);

	}
	
	public static Deallocator createDeallocator(Environment env,IClass clazz){
		MethodHeaderNode mh = new MethodHeaderNode(null,null, null, clazz.getName(), null, null);
		MethodDeclNode md = new MethodDeclNode(MethodTypeSE.DESTRUCTOR, mh, new BlockStmtNode(new StmtListNode()));
		Deallocator da = new Deallocator(md, clazz, clazz.getScope(), env);
		return da;
	}

	@Override
	public String getElementTypeName() {
		return "deallocator";
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
