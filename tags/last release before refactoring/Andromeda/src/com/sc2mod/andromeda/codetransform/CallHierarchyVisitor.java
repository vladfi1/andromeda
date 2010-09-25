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

import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.AndromedaFileInfo;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.semAnalysis.ForeachSemantics;
import com.sc2mod.andromeda.semAnalysis.NameResolver;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.DeleteStatement;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConstructorInvocationStatement;
import com.sc2mod.andromeda.syntaxNodes.ForEachStatement;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.VariableAssignDecl;

public class CallHierarchyVisitor extends TransformationVisitor {
	
	//XPilot: these are not used...
	//boolean readAccess = true;
	//boolean writeAccess;
	
	private CallHierarchyExpressionVisitor exprVisitor;
	private StaticInitVisitor staticInitVisitor;
	
	public CallHierarchyVisitor(Options options, NameResolver nameResolver) {
		super(new CallHierarchyExpressionVisitor(options), options,false,nameResolver);
		exprVisitor = (CallHierarchyExpressionVisitor) super.exprVisitor;
		staticInitVisitor = new StaticInitVisitor(this);
	}

	/*
	public void resetMarked() {
		marked.clear();
	}
	*/
	
	//*********** GLOBAL CONSTRUCTS (just loop through) **********
	@Override
	public void visit(AndromedaFile andromedaFile) {
		int inclType = andromedaFile.getFileInfo().getInclusionType();
		if(inclType == AndromedaFileInfo.TYPE_NATIVE || inclType == AndromedaFileInfo.TYPE_LANGUAGE) {
			//Natives and libraries do not get parsed (only if they are called)
			return;
		}
		//XPilot: must visit the static inits of libraries
		if(inclType == AndromedaFileInfo.TYPE_LIBRARY) {
			staticInitVisitor.visit(andromedaFile);
			return;
		}
		
		andromedaFile.childrenAccept(this);
	}

	//*********** Methods **********
	@Override
	public void visit(VariableAssignDecl vad) {
		//Marked? do nothing
		VarDecl vd = (VarDecl) vad.getName().getSemantics();
		if(vd.isMarked()) {
			return;
		}
		
		vd.mark();
		
		//Do init
		super.visit(vad);

		//An inited variable is written
		((VarDecl)vad.getName().getSemantics()).registerAccess(true);
	}
	
	//Xpilot: added
	@Override
	public void visit(StaticInitDeclaration s) {
		//Get the function body
		Statement body = s.getBody();
		
		//Set current function
		Function f = (Function)s.getSemantics();
		
		//XPilot: Not sure if static inits are ever marked
		
		//Function already marked? Return
		if(f.isMarked()) return;
		
		//Now check the body for calls
		body.accept(this);
		
		//Mark function as visited
		f.mark();
		
		//Check for unused locals
		UnusedFinder.checkForUnusedLocals(f, options);
	}
	
	@Override
	public void visit(MethodDeclaration methodDeclaration) {
		//Set current function
		Function f = (Function)methodDeclaration.getSemantics();
		
		//Function already marked? Return
		if(f.isMarked()) return;
		
		Statement body = methodDeclaration.getBody();
		
		if(body != null) {
			//Now check the body for calls
			body.accept(this);
			
			//Check for unused locals
			UnusedFinder.checkForUnusedLocals(f,options);
		}
		
		//Mark function as visited
		f.mark();
	}
	
	//************** STATEMENTS (just loop through and replace expressions if necessary) ********
	
	@Override
	public void visit(DeleteStatement deleteStatement) {
		//Visit children
		super.visit(deleteStatement);
		
		//Register destructor invocation
		exprVisitor.registerInvocation((Invocation) deleteStatement.getSemantics());
	}
	
	@Override
	public void visit(ForEachStatement forEachStatement) {
		ForeachSemantics semantics = (ForeachSemantics) forEachStatement.getSemantics();
		
		//The hasNext next and getIterator methods are invoked
		exprVisitor.registerInvocation(semantics.getGetIterator());
		exprVisitor.registerInvocation(semantics.getHasNext());
		exprVisitor.registerInvocation(semantics.getNext());
		
		//Visit children
		super.visit(forEachStatement);
	}
	
	//XPilot: added
	@Override
	public void visit(ExplicitConstructorInvocationStatement explicitConstructorInvocationStatement) {
		//Visit children
		super.visit(explicitConstructorInvocationStatement);
		
		ConstructorInvocation ci = (ConstructorInvocation)explicitConstructorInvocationStatement.getSemantics();
		exprVisitor.registerInvocation(ci);
	}
	
	//************** EXPRESSIONS (just loop through and replace expressions if necessary) ********
}
