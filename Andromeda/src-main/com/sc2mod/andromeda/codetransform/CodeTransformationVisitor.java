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
import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.BreakStatement;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStatement;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.ForEachStatement;
import com.sc2mod.andromeda.syntaxNodes.ForStatement;
import com.sc2mod.andromeda.syntaxNodes.ReturnStatement;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.WhileStatement;

public class CodeTransformationVisitor extends TransformationVisitor {

	boolean replaceAssignmentByAccessor;
	ExpressionTransformationVisitor rValueVisitor;
	UglyExprTransformer exprTransformer;
	private ArrayList<ArrayList<Statement>> insertBeforeReturn = new ArrayList<ArrayList<Statement>>();

	public CodeTransformationVisitor(Configuration options, TypeProvider typeProvider, NameResolver nameResolver) {
		super(new CodeExpressionTransformationVisitor(options, typeProvider),
				options, true, nameResolver);
	}

	protected void pushBeforeReturn(
			ArrayList<Statement> insertBeforeStmts2) {
		ArrayList<Statement> al = new ArrayList<Statement>(insertBeforeStmts2
				.size());
		al.addAll(insertBeforeStmts2);
		insertBeforeReturn.add(al);
	}

	protected void pushBeforeReturn(Statement s) {
		ArrayList<Statement> al = new ArrayList<Statement>(1);
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
	public void visit(ForStatement forStatement) {

		pushBeforeReturn();

		boolean pushContinueBefore = pushLoopContinue;
		pushLoopContinue = false;

		Statement s = forStatement.getForUpdate();
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
	public void visit(ForEachStatement forEachStatement) {
		ForeachSemantics semantics = (ForeachSemantics) forEachStatement.getSemantics();
		FieldAccess l = varProvider.getImplicitLocalVar(semantics.getIteratorType());
		semantics.setIterator(l);
		if(semantics.doDestroyAfter()){
			//If we want to destroy after the loop, we need to insert it before each return
			Statement delStatement = syntaxGenerator.genDeleteStatement(l);
			pushBeforeReturn(delStatement);
			semantics.setDelStatement(delStatement);
		} else {
			
			pushBeforeReturn();
				
		}
		super.visit(forEachStatement);
		popBeforeReturn();
	}
	
	@Override
	public void visit(DoWhileStatement doWhileStatement) {
		pushBeforeReturn();
		super.visit(doWhileStatement);
		popBeforeReturn();
	}
	
	@Override
	public void visit(WhileStatement whileStatement) {
		pushBeforeReturn();
		super.visit(whileStatement);
		popBeforeReturn();
	}
	
	@Override
	public void visit(ReturnStatement returnStatement) {
		//Prepend ALL statements from all frames
		for(ArrayList<Statement> stmts : insertBeforeReturn){
			for(Statement s: stmts){
				addStatementBefore(s);
			}
		}
		//Do default stuff
		super.visit(returnStatement);
	}

}
