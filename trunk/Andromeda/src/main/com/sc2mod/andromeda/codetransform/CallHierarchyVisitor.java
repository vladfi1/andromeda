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

import java.util.HashMap;
import java.util.HashSet;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.access.ConstructorInvocation;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.semAnalysis.ForeachSemantics;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConsCallStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ForEachStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;

public class CallHierarchyVisitor extends TransformationVisitor {
	
	//XPilot: these are not used...
	//boolean readAccess = true;
	//boolean writeAccess;
	//TODO: Assign a good starting size to this and other hash maps. This can be done by counting the elements in the environment.
	private HashSet<SemanticsElement> marked = new HashSet<SemanticsElement>();
	
	private void mark(SemanticsElement elem){
		marked.add(elem);
	}
	
	private boolean isMarked(SemanticsElement elem){
		return marked.contains(elem);
	}
	
	
	private CallHierarchyExpressionVisitor exprVisitor;
	private StaticInitVisitor staticInitVisitor;
	
	public CallHierarchyVisitor(Configuration options) {
		super(new CallHierarchyExpressionVisitor(options), options,false);
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
	public void visit(SourceFileNode andromedaFile) {
		InclusionType inclType = andromedaFile.getFileInfo().getInclusionType();
		if(inclType == InclusionType.NATIVE || inclType == InclusionType.LANGUAGE) {
			//Natives and libraries do not get parsed (only if they are called)
			return;
		}
		//XPilot: must visit the static inits of libraries
		if(inclType == InclusionType.LIBRARY) {
			staticInitVisitor.visit(andromedaFile);
			return;
		}
		
		andromedaFile.childrenAccept(this);
	}

	//*********** Methods **********
	@Override
	public void visit(VarAssignDeclNode vad) {
		//Marked? do nothing
		VarDecl vd = (VarDecl) vad.getName().getSemantics();
		if(isMarked(vd)) {
			return;
		}
		
		mark(vd);
		
		//Do init
		super.visit(vad);

		//An inited variable is written
		((VarDecl)vad.getName().getSemantics()).registerAccess(true);
	}
	
	//Xpilot: added
	@Override
	public void visit(StaticInitDeclNode s) {
		//Get the function body
		StmtNode body = s.getBody();
		
		//Set current function
		Function f = (Function)s.getSemantics();
		
		//XPilot: Not sure if static inits are ever marked
		
		//Function already marked? Return
		if(isMarked(f)) return;
		
		//Now check the body for calls
		body.accept(this);
		
		//Mark function as visited
		mark(f);
		
		//Check for unused locals
		//TODO: Redo unused finding
		//UnusedFinder.checkForUnusedLocals(f, options);
	}
	
	@Override
	public void visit(MethodDeclNode methodDeclaration) {
		//Set current function
		Function f = (Function)methodDeclaration.getSemantics();
		
		//Function already marked? Return
		if(isMarked(f)) return;
		
		StmtNode body = methodDeclaration.getBody();
		
		if(body != null) {
			//Now check the body for calls
			body.accept(this);
			
			//Check for unused locals
			//TODO: Redo unused finding
			//UnusedFinder.checkForUnusedLocals(f,options);
		}
		
		//Mark function as visited
		mark(f);
	}
	
	//************** STATEMENTS (just loop through and replace expressions if necessary) ********
	
	@Override
	public void visit(DeleteStmtNode deleteStatement) {
		//Visit children
		super.visit(deleteStatement);
		
		//Register destructor invocation
		exprVisitor.registerInvocation((Invocation) deleteStatement.getSemantics());
	}
	
	@Override
	public void visit(ForEachStmtNode forEachStatement) {
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
	public void visit(ExplicitConsCallStmtNode explicitConstructorInvocationStatement) {
		//Visit children
		super.visit(explicitConstructorInvocationStatement);
		
		ConstructorInvocation ci = (ConstructorInvocation)explicitConstructorInvocationStatement.getSemantics();
		exprVisitor.registerInvocation(ci);
	}
	
	//************** EXPRESSIONS (just loop through and replace expressions if necessary) ********
}
