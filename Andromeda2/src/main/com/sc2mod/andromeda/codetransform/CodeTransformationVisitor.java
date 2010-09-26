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

import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.semAnalysis.ForeachSemantics;
import com.sc2mod.andromeda.semAnalysis.NameResolver;
import com.sc2mod.andromeda.syntaxNodes.AccessTypeSE;
import com.sc2mod.andromeda.syntaxNodes.BreakStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ForEachStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ForStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;

public class CodeTransformationVisitor extends TransformationVisitor {

	boolean replaceAssignmentByAccessor;
	ExpressionTransformationVisitor rValueVisitor;
	UglyExprTransformer exprTransformer;
	private ArrayList<ArrayList<StmtNode>> insertBeforeReturn = new ArrayList<ArrayList<StmtNode>>();

	public CodeTransformationVisitor(Configuration options, TypeProvider typeProvider, NameResolver nameResolver) {
		super(new CodeExpressionTransformationVisitor(options, typeProvider),
				options, true, nameResolver);
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
	}

	@Override
	public void visit(ForEachStmtNode forEachStatement) {
		ForeachSemantics semantics = (ForeachSemantics) forEachStatement.getSemantics();
		FieldAccessExprNode l = varProvider.getImplicitLocalVar(semantics.getIteratorType());
		semantics.setIterator(l);
		if(semantics.doDestroyAfter()){
			//If we want to destroy after the loop, we need to insert it before each return
			StmtNode delStatement = syntaxGenerator.genDeleteStatement(l);
			pushBeforeReturn(delStatement);
			semantics.setDelStatement(delStatement);
		} else {
			
			pushBeforeReturn();
				
		}
		super.visit(forEachStatement);
		popBeforeReturn();
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
