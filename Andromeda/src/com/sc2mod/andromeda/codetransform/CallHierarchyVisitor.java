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

import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.AndromedaFileInfo;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.semAnalysis.ForeachSemantics;
import com.sc2mod.andromeda.semAnalysis.LoopSemantics;
import com.sc2mod.andromeda.semAnalysis.NameResolver;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.DeleteStatement;
import com.sc2mod.andromeda.syntaxNodes.ForEachStatement;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.VariableAssignDecl;

public class CallHierarchyVisitor extends TransformationVisitor{
	
	boolean readAccess = true;
	boolean writeAccess;
	private CallHierarchyExpressionVisitor exprVisitor;
	
	
	public CallHierarchyVisitor(Options options, NameResolver nameResolver) {
		super(new CallHierarchyExpressionVisitor(options), options,false,nameResolver);
		exprVisitor = (CallHierarchyExpressionVisitor) super.exprVisitor;
	}
	


	//*********** GLOBAL CONSTRUCTS (just loop through) **********
	@Override
	public void visit(AndromedaFile andromedaFile) {
		int inclType = andromedaFile.getFileInfo().getInclusionType();
		if(inclType == AndromedaFileInfo.TYPE_LIBRARY || inclType == AndromedaFileInfo.TYPE_NATIVE || inclType == AndromedaFileInfo.TYPE_LANGUAGE){
			//Natives and libraries do not get parsed (only if they are called)
			return;
		}
		andromedaFile.childrenAccept(this);
				
	}

	//*********** Methods **********
	@Override
	public void visit(VariableAssignDecl vad){
		//Marked? do nothing
		VarDecl vd = (VarDecl) vad.getName().getSemantics();
		if(vd.isMarked()){
			return;
		}
		
		vd.mark();
		
		//Do init
		super.visit(vad);

		//An inited variable is written
		((VarDecl)vad.getName().getSemantics()).registerAccess(true);
	}
	
	@Override
	public void visit(MethodDeclaration methodDeclaration) {

		//Get function body, if this function has none (abstract/inteface) we don't have to do anything
		Statement body = methodDeclaration.getBody();
		if(body == null) return;
		
		//Set current function
		Function f = (Function)methodDeclaration.getSemantics();
		
		//Function already marked? Return
		if(f.isMarked()) return;
		
		//Now check the body for calls
		body.accept(this);		
		
		//Mark function as visited
		f.mark();
		
		//Check for unused locals
		UnusedFinder.checkForUnusedLocals(f,options);
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
	
	//************** EXPRESSIONS (just loop through and replace expressions if necessary) ********
	
}