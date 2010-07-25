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

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.AndromedaFileInfo;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccess;
import com.sc2mod.andromeda.syntaxNodes.ClassInstanceCreationExpression;
import com.sc2mod.andromeda.syntaxNodes.DeleteStatement;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConstructorInvocationStatement;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocation;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.FuncNameObject;

public class CallHierarchyExpressionVisitor extends ExpressionTransformationVisitor {

	public CallHierarchyExpressionVisitor(Options options) {
		super(options);
	}
	
	@Override
	public void visit(LiteralExpression literalExpression) {
		DataObject o = literalExpression.getLiteral().getValue();
		if(o instanceof FuncNameObject){
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
		
		/*
		if(inv instanceof ConstructorInvocation) {
			System.out.println(invocationTarget.getDescription() + "(" + invocationTarget.getSignature() + ")");
			System.out.println(invocationTarget.getDefinition());
		}
		*/
		
		if(!alreadyChecked&&invocationTarget.getScope().getInclusionType() != AndromedaFileInfo.TYPE_NATIVE){
			//Only check it if it is no function defined in blizzard's libs
			invocationTarget.getDefinition().accept(parent);
		}

		//Check if we can inline this function call
		int inlineType = InlineDecider.decide(inv);
		if(inlineType != InlineDecider.INLINE_NO){
			invocationTarget.addInline();
			throw new Error("inline not yet supported.");
		} else {
			invocationTarget.addInvocation();
		}
	}
	
	@Override
	public void visit(MethodInvocation methodInvocation) {
		//Do children
		super.visit(methodInvocation);
		
		//Visit the called method (we do a depth first search)
		Invocation inv = (Invocation) methodInvocation.getSemantics();
		
		registerInvocation(inv);

	}
		
	@Override
	public void visit(FieldAccess fieldAccess) {
		VarDecl vd = (VarDecl) fieldAccess.getSemantics();
		
		//System.out.println(fieldAccess.getName());
		
		if(isRead) vd.registerAccess(false);
		if(isWrite) vd.registerAccess(true);
		
		//Do children
		super.visit(fieldAccess);
		
		//If this is a global decl and it has an init, parse this
		if(vd.isGlobalField()&&vd.isInitDecl()){
			vd.getDeclarator().accept(parent);
		}
	}

	@Override
	public void visit(ArrayAccess arrayAccess) {
		VarDecl vd = (VarDecl) arrayAccess.getSemantics();
		
		//System.out.println(((FieldAccess)arrayAccess.getLeftExpression()).getName());
		
		if(isRead) vd.registerAccess(false);
		if(isWrite) vd.registerAccess(true);
		
		//Do children
		super.visit(arrayAccess);
		
		//If this is a global decl and it has an init, parse this
		if(vd.isGlobalField()&&vd.isInitDecl()){
			vd.getDeclarator().accept(parent);
		}
	}
	
	@Override
	public void visit(ClassInstanceCreationExpression c) {
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
	public void visit(DeleteStatement deleteStatement) {
		//Children
		super.visit(deleteStatement);
		
		Invocation i = (Invocation)deleteStatement.getSemantics();
		
		registerInvocation(i);
	}

}
