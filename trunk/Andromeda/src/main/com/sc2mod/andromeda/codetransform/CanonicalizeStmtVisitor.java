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

import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.semAnalysis.ForeachSemantics;
import com.sc2mod.andromeda.semAnalysis.LoopSemantics;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ForEachStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ForStmtNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;

public class CanonicalizeStmtVisitor extends TransformationVisitor {

	boolean replaceAssignmentByAccessor;
	TransformationExprVisitor rValueVisitor;
	UglyExprTransformer exprTransformer;
	private ArrayList<ArrayList<StmtNode>> insertBeforeReturn = new ArrayList<ArrayList<StmtNode>>();

	public CanonicalizeStmtVisitor(Configuration options, TypeProvider typeProvider) {
		super(new CanonicalizeExprVisitor(options, typeProvider),
				typeProvider, options, true);
	}

	protected void pushBeforeReturn(
			ArrayList<StmtNode> insertBeforeStmts2) {
		ArrayList<StmtNode> al = new ArrayList<StmtNode>(insertBeforeStmts2
				.size());
		al.addAll(insertBeforeStmts2);
		insertBeforeReturn.add(al);
	}

	protected void pushBeforeReturn(StmtNode s) {
		ArrayList<StmtNode> al = new ArrayList<StmtNode>(1);
		al.add(s);
		insertBeforeReturn.add(al);
	}

	protected void pushBeforeReturn() {
		insertBeforeReturn.add(TransformationVisitor.EMPTY_LIST);
	}

	protected void popBeforeReturn() {
		insertBeforeReturn.remove(insertBeforeReturn.size() - 1);
	}

	@Override
	public void visit(ForStmtNode forStatement) {

		pushBeforeReturn();

		boolean pushContinueBefore = pushLoopContinue;
		pushLoopContinue = false;

		StmtNode s = forStatement.getForUpdate();
		if (s != null) {
			s.accept(this);
			// Insert the update block before each continue
			pushBeforeContinue(s);
		} else {
			// Insert nothing
			pushBeforeContinue();
		}
		super.visit(forStatement);

		popBeforeContinue();
		popBeforeReturn();

		pushLoopContinue = pushContinueBefore;
		
		//Now we transform the loop into a while loop
		/*
		 *	for(<init> ; <cond> ; <update>){
		 *		<body>
		 *  }
		 *  
		 *  =>
		 *  
		 *  <init>
		 *  while(<cond>){
		 *  	<body>
		 *  	<update> (if control flow reaches end of loop)
		 *  }
		 */
		
		//prepend for init
		StmtNode forInit = forStatement.getForInit();
		addStatementBefore(forInit);
		
		//add update behind body, unwrap if necessary
		LoopSemantics semantics = forStatement.getSemantics();
		if(forStatement.getSemantics().doesControlFlowReachEnd()){
			forStatement.getThenStatement().getStatements().add(forStatement.getForUpdate());
		}
		
		//while loop
		WhileStmtNode loop = syntaxGenerator.createWhileLoop(forStatement.getCondition(), forStatement.getThenStatement(), new LoopSemantics(semantics));
		
		//replace for loop by while loop
		replaceStatement(loop);
		
	}

	@Override
	public void visit(ForEachStmtNode forEachStatement) {
		ForeachSemantics semantics = (ForeachSemantics) forEachStatement.getSemantics();
		NameExprNode l = varProvider.getImplicitLocalVar(semantics.getIteratorType());
		semantics.setIterator(l);
		StmtNode delStatement = null;
		if(semantics.doDestroyAfter()){
			//If we want to destroy after the loop, we need to insert it before each return
			delStatement = syntaxGenerator.genDeleteStatement(l);
			pushBeforeReturn(delStatement);

		} else {
			
			pushBeforeReturn();
				
		}
		super.visit(forEachStatement);
		popBeforeReturn();
		
		//Replace the loop by a while loop
		/*
		 * for(IterType a : b){ <body> }
		 * 
		 * =>
		 * 
		 * iterator = b.getIterator();
		 * while(iterator.hasNext()){
		 * 	IterType a = iterator.next();
		 *  <body>
		 * }
		 * delete iterator; (if want delete)
		 * 
		 */
		//b.getIterator()
		ExprNode getIterator = syntaxGenerator.createMethodInvocation(forEachStatement.getExpression(), "getIterator", SyntaxGenerator.EMPTY_EXPRESSIONS, semantics.getGetIterator());
		
		//iterator = b.getIterator();
		StmtNode init = syntaxGenerator.genAssignStatement(l, getIterator, AssignOpSE.EQ);
		
		//iterator.next()
		ExprNode next = syntaxGenerator.createMethodInvocation(l, "next", SyntaxGenerator.EMPTY_EXPRESSIONS, semantics.getNext());
		
		//IterType a = iterator.getNext();
		StmtNode nextStmt = syntaxGenerator.genLocalVarAssignDeclStmt(forEachStatement.getIteratorType(), forEachStatement.getIterator(), next);
		
		//prepend to body
		BlockStmtNode body = forEachStatement.getThenStatement();
		body.getStatements().add(0,nextStmt);
		
		//iterator.hasNext()
		ExprNode condition = syntaxGenerator.createMethodInvocation(l, "getNext", SyntaxGenerator.EMPTY_EXPRESSIONS, semantics.getHasNext());
		
		//loop
		WhileStmtNode n = syntaxGenerator.createWhileLoop(condition, body, new LoopSemantics(semantics));

		
		//add init before, replace foreach loop by while loop
		addStatementBefore(init);
		
		//if the user wants to delete after the loop, insert the loop and delete, otherwise only the loop
		if(semantics.doDestroyAfter()){
			addStatementBefore(n);
			replaceStatement(delStatement);
		} else {
			replaceStatement(n);
		}
		
	}
	
	@Override
	public void visit(DoWhileStmtNode doWhileStatement) {
		pushBeforeReturn();
		super.visit(doWhileStatement);
		popBeforeReturn();
	}
	
	@Override
	public void visit(WhileStmtNode whileStatement) {
		pushBeforeReturn();
		super.visit(whileStatement);
		popBeforeReturn();
	}
	
	@Override
	public void visit(ReturnStmtNode returnStatement) {
		//Prepend ALL statements from all frames
		for(ArrayList<StmtNode> stmts : insertBeforeReturn){
			for(StmtNode s: stmts){
				addStatementBefore(s);
			}
		}
		//Do default stuff
		super.visit(returnStatement);
	}

}
