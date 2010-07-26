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

import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.AndromedaFileInfo;
import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.semAnalysis.NameResolver;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclaration;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.BlockStatement;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ContinueStatement;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStatement;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConstructorInvocationStatement;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.ExpressionStatement;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.ForEachStatement;
import com.sc2mod.andromeda.syntaxNodes.ForStatement;
import com.sc2mod.andromeda.syntaxNodes.FunctionDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclaration;
import com.sc2mod.andromeda.syntaxNodes.IfThenElseStatement;
import com.sc2mod.andromeda.syntaxNodes.IncludedFile;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclaration;
import com.sc2mod.andromeda.syntaxNodes.LocalVariableDeclarationStatement;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ReturnStatement;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.StatementList;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VariableAssignDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarator;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;
import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;
import com.sc2mod.andromeda.syntaxNodes.WhileStatement;

/**
 * General superclass for all visitors that can do transformations to the syntax tree.
 * (Expressions can be exchanged, statements can be appended before the processed statement)
 * @author J. 'gex' Finis 
 */
public abstract class TransformationVisitor extends VisitorAdaptor {

	protected static ArrayList<Statement> EMPTY_LIST = new ArrayList<Statement>(0);
	private ArrayList<Statement> insertBeforeStmts = new ArrayList<Statement>();
	private ArrayList<ArrayList<Statement>> insertBeforeContinue = new ArrayList<ArrayList<Statement>>();

	protected void pushBeforeContinue(ArrayList<Statement> insertBeforeStmts2) {
		ArrayList<Statement> al = new ArrayList<Statement>(insertBeforeStmts2.size());
		al.addAll(insertBeforeStmts2);
		insertBeforeContinue.add(al);
	}
	
	protected void pushBeforeContinue(Statement s){
		ArrayList<Statement> al = new ArrayList<Statement>(1);
		al.add(s);
		insertBeforeContinue.add(al);
	}
	
	protected void pushBeforeContinue(){
		insertBeforeContinue.add(EMPTY_LIST);
	}
	
	protected void popBeforeContinue(){
		insertBeforeContinue.remove(insertBeforeContinue.size()-1);
	}
	

	
	public void addStatementBefore(Statement statement) {
		insertBeforeStmts.add(statement);
	}

	Expression replaceExpression;
	SyntaxGenerator syntaxGenerator;
	ImplicitLocalVarProvider varProvider = new ImplicitLocalVarProvider();
	protected ExpressionTransformationVisitor exprVisitor;
	protected Options options;
	protected boolean pushLoopContinue = true;
	private boolean flushAfterExpression;
		
	public TransformationVisitor(ExpressionTransformationVisitor exprVisitor, Options options, boolean flushAfterExpression, NameResolver nameResolver) {
		syntaxGenerator = new SyntaxGenerator(options,nameResolver);
		this.exprVisitor = exprVisitor;
		this.options = options;
		this.flushAfterExpression = flushAfterExpression;
		this.exprVisitor.setParent(this);
		insertBeforeContinue.add(EMPTY_LIST);
	}
	
	protected void invokeExprVisitor(Expression expression, boolean inExpr){
		if(inExpr){
			exprVisitor.invokeSelf(expression);
		} else {
			expression.accept(exprVisitor);
		}
		if(flushAfterExpression)varProvider.flushInUseLocals();
	}
	
	protected void invokeExprVisitor(Expression expression){
		invokeExprVisitor(expression,false);
	}
	
	protected int insertStatementsAt(StatementList where, int index){
		int size = where.size();
		for(Statement insert: insertBeforeStmts){
			where.insertElementAt(insert,index++);
			size++;
		}
		insertBeforeStmts.clear();
		return size;
	}
	
	//*********** GLOBAL CONSTRUCTS (just loop through) **********
	@Override
	public void visit(AndromedaFile andromedaFile) {
		AndromedaFileInfo fi = andromedaFile.getFileInfo();
		
		//Natives are not transformed
		if(fi.getInclusionType()==AndromedaFileInfo.TYPE_NATIVE) return;
		andromedaFile.childrenAccept(this);
	}
	
	@Override
	public void visit(FileContent fileContent) {
		fileContent.childrenAccept(this);
	}
	
	@Override
	public void visit(IncludedFile includedFile) {		
		includedFile.childrenAccept(this);
	}
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		classDeclaration.getBody().childrenAccept(this);
	}	
	@Override
	public void visit(InterfaceDeclaration interfaceDeclaration) {
		interfaceDeclaration.getBody().accept(this);
	}	
	@Override
	public void visit(FunctionDeclaration functionDeclaration) {
		functionDeclaration.childrenAccept(this);
	}
	@Override
	public void visit(GlobalVarDeclaration globalVarDeclaration) {
		globalVarDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalInitDeclaration g) {
		g.getInitDecl().accept(this);
	}
	
	@Override
	public void visit(EnrichDeclaration enrichDeclaration) {
		enrichDeclaration.getBody().childrenAccept(this);
	}
	@Override
	public void visit(FieldDeclaration fieldDeclaration) {
		VariableDeclarators v = fieldDeclaration.getDeclaredVariables();
		v.childrenAccept(this);
	}
	
	@Override
	public void visit(LocalVariableDeclarationStatement l) {
		VariableDeclarators vds = l.getVarDeclaration().getDeclarators();
		int size = vds.size();
		for(int i=0;i<size;i++){
			VariableDeclarator vd = vds.elementAt(i);
			vd.accept(this);
		}
	}
	
	@Override
	public void visit(VariableAssignDecl variableAssignDecl) {
		invokeExprVisitor(variableAssignDecl.getInitializer(),true);
		
		if(replaceExpression!=null){
			variableAssignDecl.setInitializer(replaceExpression);
			replaceExpression = null;
		}
		//If statements were generated, we need to add those to the init code of the variable decl
		if(!insertBeforeStmts.isEmpty()){
			VarDecl decl = (VarDecl) variableAssignDecl.getSemantics();
			decl.addInitCode(insertBeforeStmts);
			insertBeforeStmts.clear();
		}

	}
	
	@Override
	public void visit(AccessorDeclaration accessorDeclaration) {
		accessorDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(StaticInitDeclaration s){
		s.childrenAccept(this);
		
		//flush implicit locals
		varProvider.flushMethodBufferToFunction((Function) s.getSemantics());
	}
	
	@Override
	public void visit(MethodDeclaration methodDeclaration) {		
		//Get function body, if this function has none (abstract/interface) we don't have to do anything
		Statement body = methodDeclaration.getBody();
		if(body == null) return;
		
		methodDeclaration.childrenAccept(this);
		
		//flush implicit locals
		varProvider.flushMethodBufferToFunction((Function) methodDeclaration.getSemantics());
	}
	
	@Override
	public void visit(WhileStatement whileStatement) {
		//Do condition
		Expression cond = whileStatement.getCondition();
		invokeExprVisitor(cond);
		if(replaceExpression!=null){
			whileStatement.setCondition(replaceExpression);
			replaceExpression = null;
		}		
		
		//We have additional statements in the condition :(
		if(!insertBeforeStmts.isEmpty()){
			
			//Create an aborting if
			whileStatement.getThenStatement().getStatements().insertElementAt(syntaxGenerator.createLoopAbortIf(whileStatement.getCondition()),0);
			
			//Insert the additional statements before that if
			insertStatementsAt(whileStatement.getThenStatement().getStatements(),0);
						
			//Replace the condition in the loop by true
			whileStatement.setCondition(syntaxGenerator.genBoolLiteralExpr(true));
			
		}
		
		//Nothing has to be inserted before a continue, return or break
		pushBeforeContinue();
		
		//Do body
		Statement s = whileStatement.getThenStatement();
		s.accept(this);
		
		popBeforeContinue();
	}
	
	@Override
	public void visit(ForEachStatement forEachStatement) {
	
		
		//Nothing has to be inserted before a continue
		pushBeforeContinue();
		
		//Do body
		Statement s = forEachStatement.getThenStatement();
		s.accept(this);
		
		//Do the init expression, we do it after the body so generated statements get prepended to the top statement list
		Expression cond = forEachStatement.getExpression();
		invokeExprVisitor(cond);
		if(replaceExpression!=null){
			forEachStatement.setCondition(replaceExpression);
			replaceExpression = null;
		}	
		
		popBeforeContinue();
	}
	
	@Override
	public void visit(IfThenElseStatement s) {
		
		//then and else
		Statement b = s.getThenStatement();
		b.accept(this);
		b = s.getElseStatement();
		if(b != null) b.accept(this);

		//Do condition
		Expression cond = s.getCondition();
		invokeExprVisitor(cond);
		if(replaceExpression!=null){
			s.setCondition(replaceExpression);
			replaceExpression = null;
		}		
		
		//Insert before condition statements are just passed to the next level,
		//so do nothing here
	
	}
	
	@Override
	public void visit(DoWhileStatement doWhileStatement) {
		//Do condition
		Expression cond = doWhileStatement.getCondition();
		invokeExprVisitor(cond);
		if(replaceExpression!=null){
			doWhileStatement.setCondition(replaceExpression);
			replaceExpression = null;
		}		
		
		//We have additional statements in the condition :(
		if(!insertBeforeStmts.isEmpty()){
			
			//Insert the additional statement before continues
			pushBeforeContinue(insertBeforeStmts);
			
			//Insert the additional statements at the end of the body
			StatementList body = doWhileStatement.getThenStatement().getStatements();
			insertStatementsAt(body,body.size());
						
		} else {
			//Nothing to add before continue
			pushBeforeContinue();
		}
		
		//Do body
		Statement s = doWhileStatement.getThenStatement();
		s.accept(this);
		popBeforeContinue();
	}

	@Override
	public void visit(ForStatement forStatement) {
		//Do condition
		Expression cond = forStatement.getCondition();
		invokeExprVisitor(cond);
		if(replaceExpression!=null){
			forStatement.setCondition(replaceExpression);
			replaceExpression = null;
		}		
		
		//We have additional statements in the condition :(
		if(!insertBeforeStmts.isEmpty()){
			
			//Create an aborting if
			forStatement.getThenStatement().getStatements().insertElementAt(syntaxGenerator.createLoopAbortIf(forStatement.getCondition()),0);
			
			//Insert the additional statements before that if
			insertStatementsAt(forStatement.getThenStatement().getStatements(),0);
						
			//Replace the condition in the loop by true
			forStatement.setCondition(syntaxGenerator.genBoolLiteralExpr(true));
		}
		
		//Init
		Statement s = forStatement.getForInit();
		
		//Update
		if(s != null) s.accept(this);
		s = forStatement.getForUpdate();		
		if(pushLoopContinue){
			pushBeforeContinue();
		}
		
		//Body
		s = forStatement.getThenStatement();
		if(s != null) s.accept(this);
		
		if(pushLoopContinue){
			popBeforeContinue();
		}
	}
	
	@Override
	public void visit(BlockStatement blockStatement) {
		blockStatement.childrenAccept(this);
	}
	
	@Override
	public void visit(StatementList statementList) {
		int size = statementList.size();
		for(int i=0;i<size;i++){
			Statement s = statementList.elementAt(i);
			s.accept(this);
			if(!insertBeforeStmts.isEmpty()){
				for(Statement insert: insertBeforeStmts){
					statementList.insertElementAt(insert,i++);
					size++;
				}
				insertBeforeStmts.clear();
			}
		}
	}
	
	@Override
	public void visit(ContinueStatement continueStatement) {
		//Add statements from the stack before this continue statement
		insertBeforeStmts.addAll(insertBeforeContinue.get(insertBeforeContinue.size()-1));
	}
	
	@Override
	public void visit(ReturnStatement returnStatement) {
		Expression result = returnStatement.getResult();
		if(result != null){
			invokeExprVisitor(result);
			if(replaceExpression!=null){
				returnStatement.setResult(replaceExpression);
				replaceExpression = null;
			}
		}
	}
	
	@Override
	public void visit(ExpressionStatement e){
		invokeExprVisitor(e.getExpression());
		if(replaceExpression!=null){
			e.setExpression(replaceExpression);
			replaceExpression = null;
		}
	}

	//XPilot: added
	@Override
	public void visit(ExplicitConstructorInvocationStatement explicitConstructorInvocationStatement) {
		//invokeExprVisitor(explicitConstructorInvocationStatement.getExpression());
		exprVisitor.invokeSelf(explicitConstructorInvocationStatement.getExpression());
		exprVisitor.invokeSelf(explicitConstructorInvocationStatement.getArguments());
		//explicitConstructorInvocationStatement.accept(exprVisitor);
	}
}
