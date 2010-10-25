/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codetransform;

import java.util.ArrayList;

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.SourceFileInfo;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.NewExprNode;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.FuncNameObject;

public class CallHierarchyExpressionVisitor extends ExpressionTransformationVisitor {

	public CallHierarchyExpressionVisitor(Configuration options) {
		super(options);
	}
	
	@Override
	public void visit(LiteralExprNode literalExpression) {
		DataObject o = literalExpression.getLiteral().getValue();
		if(o instanceof FuncNameObject) {
			//We have an inlined function name. This function name could be called!
			AbstractFunction f = ((FuncNameObject)o).getFunction();
			f.addInvocation();
			f.getDefinition().accept(parent);
		}
	}
	
	
	//**************** INVOCATIONS (WHERE THIS VISITOR DOES ITS JOB) *************
	
	public void registerInvocation(Invocation inv) {
		if(inv == null) return;
		//If it is already used we were already here
		boolean alreadyChecked = inv.isUsed();
		
		//The invocation is used indeed
		inv.use();
		
		AbstractFunction invocationTarget = inv.getWhichFunction();
		
		//XPilot: invocationTarget is null for default constructors
		if(invocationTarget == null) return;
		
		if(!alreadyChecked&&invocationTarget.getScope().getInclusionType() != InclusionType.NATIVE) {
			//Only check it if it is no function defined in blizzard's libs
			invocationTarget.getDefinition().accept(parent);
		}

		//Check if we can inline this function call
		int inlineType = InlineDecider.decide(inv);
		if(inlineType != InlineDecider.INLINE_NO) {
			invocationTarget.addInline();
			throw new Error("inline not yet supported.");
		} else {
			invocationTarget.addInvocation();
			//visit(invocationTarget);
		}
	}
	
	@Override
	public void visit(MethodInvocationExprNode methodInvocation) {
		//Do children
		super.visit(methodInvocation);
		
		//Visit the called method (we do a depth first search)
		Invocation inv = (Invocation) methodInvocation.getSemantics();
		
		registerInvocation(inv);

	}
		
	@Override
	public void visit(FieldAccessExprNode fieldAccess) {
		VarDecl vd = (VarDecl) fieldAccess.getSemantics();
		
		//System.out.println(fieldAccess.getName());
		
		if(isRead) vd.registerAccess(false);
		if(isWrite) vd.registerAccess(true);
		
		//Do children
		super.visit(fieldAccess);
		
		//If this is a global decl and it has an init, parse this
		if(vd.isGlobalField()&&vd.isInitDecl()) {
			vd.getDeclarator().accept(parent);
		}
	}

	@Override
	public void visit(ArrayAccessExprNode arrayAccess) {
		VarDecl vd = (VarDecl) arrayAccess.getSemantics();
		
		//System.out.println(((FieldAccess)arrayAccess.getLeftExpression()).getName());
		
		if(isRead) vd.registerAccess(false);
		if(isWrite) vd.registerAccess(true);
		
		//Do children
		super.visit(arrayAccess);
		
		//If this is a global decl and it has an init, parse this
		if(vd.isGlobalField()&&vd.isInitDecl()) {
			vd.getDeclarator().accept(parent);
		}
	}
	
	@Override
	public void visit(NewExprNode c) {
		//Children
		super.visit(c);
		
		//Register class instantiation
		ConstructorInvocation ci = (ConstructorInvocation) c.getSemantics();
		Class cl = (Class)c.getInferedType();
		cl.registerInstantiation();
		
		//Register the constructor invocation
		registerInvocation(ci);
	}
	
	@Override
	public void visit(DeleteStmtNode deleteStatement) {
		//Children
		super.visit(deleteStatement);
		
		Invocation i = (Invocation)deleteStatement.getSemantics();
		
		registerInvocation(i);
	}

}
