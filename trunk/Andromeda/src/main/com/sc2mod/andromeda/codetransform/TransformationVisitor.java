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

import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.SourceFileInfo;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclNode;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ContinueStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConsCallStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ForEachStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ForStmtNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IfStmtNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;

/**
 * General superclass for all visitors that can do transformations to the syntax tree.
 * (Expressions can be exchanged, statements can be appended before the processed statement)
 * @author J. 'gex' Finis 
 */
public abstract class TransformationVisitor extends VoidVisitorAdapter {

	protected static ArrayList<StmtNode> EMPTY_LIST = new ArrayList<StmtNode>(0);
	private ArrayList<StmtNode> insertBeforeStmts = new ArrayList<StmtNode>();
	private ArrayList<ArrayList<StmtNode>> insertBeforeContinue = new ArrayList<ArrayList<StmtNode>>();
	private StmtNode replaceStatement;

	protected void pushBeforeContinue(ArrayList<StmtNode> insertBeforeStmts2) {
		ArrayList<StmtNode> al = new ArrayList<StmtNode>(insertBeforeStmts2.size());
		al.addAll(insertBeforeStmts2);
		insertBeforeContinue.add(al);
	}
	
	protected void pushBeforeContinue(StmtNode s){
		ArrayList<StmtNode> al = new ArrayList<StmtNode>(1);
		al.add(s);
		insertBeforeContinue.add(al);
	}
	
	protected void pushBeforeContinue(){
		insertBeforeContinue.add(EMPTY_LIST);
	}
	
	protected void popBeforeContinue(){
		insertBeforeContinue.remove(insertBeforeContinue.size()-1);
	}
	
	protected void replaceStatement(StmtNode statement) {
		replaceStatement = statement;
	}
	
	public void addStatementBefore(StmtNode statement) {
		if(statement instanceof BlockStmtNode){
			//if it is a block statement unwrap it
			StmtListNode stmts = statement.getStatements();
			int length = stmts.size();
			for(int i = 0;i<length;i++){
				insertBeforeStmts.add(stmts.elementAt(i));
			}
		} else {
			insertBeforeStmts.add(statement);
		}
	}

	ExprNode replaceExpression;
	SyntaxGenerator syntaxGenerator;
	ImplicitLocalVarProvider varProvider = new ImplicitLocalVarProvider();
	protected TransformationExprVisitor exprVisitor;
	protected Configuration options;
	protected boolean pushLoopContinue = true;
	private boolean flushAfterExpression;
		
	public TransformationVisitor(TransformationExprVisitor exprVisitor, Configuration options, boolean flushAfterExpression) {
		syntaxGenerator = new SyntaxGenerator();
		this.exprVisitor = exprVisitor;
		this.options = options;
		this.flushAfterExpression = flushAfterExpression;
		this.exprVisitor.setParent(this);
		insertBeforeContinue.add(EMPTY_LIST);
	}
	
	protected void invokeExprVisitor(ExprNode expression, boolean inExpr){
		if(inExpr){
			exprVisitor.invokeSelf(expression);
		} else {
			expression.accept(exprVisitor);
		}
		if(flushAfterExpression)varProvider.flushInUseLocals();
	}
	
	protected void invokeExprVisitor(ExprNode expression){
		invokeExprVisitor(expression,false);
	}
	
	protected int insertStatementsAt(StmtListNode where, int index){
		int size = where.size();
		for(StmtNode insert: insertBeforeStmts){
			where.insertElementAt(insert,index++);
			size++;
		}
		insertBeforeStmts.clear();
		return size;
	}
	
	//*********** GLOBAL CONSTRUCTS (just loop through) **********
	@Override
	public void visit(SourceFileNode andromedaFile) {
		SourceFileInfo fi = andromedaFile.getFileInfo();
		
		//Natives are not transformed
		if(fi.getInclusionType()==InclusionType.NATIVE) return;
		andromedaFile.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalStructureListNode fileContent) {
		fileContent.childrenAccept(this);
	}
	
	@Override
	public void visit(IncludeNode includedFile) {		
		includedFile.childrenAccept(this);
	}
	@Override
	public void visit(ClassDeclNode classDeclaration) {
		classDeclaration.getBody().childrenAccept(this);
	}	
	@Override
	public void visit(InterfaceDeclNode interfaceDeclaration) {
		interfaceDeclaration.getBody().accept(this);
	}	
	@Override
	public void visit(GlobalFuncDeclNode functionDeclaration) {
		functionDeclaration.childrenAccept(this);
	}
	@Override
	public void visit(GlobalVarDeclNode globalVarDeclaration) {
		globalVarDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalStaticInitDeclNode g) {
		g.getInitDecl().accept(this);
	}
	
	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		enrichDeclaration.getBody().childrenAccept(this);
	}
	@Override
	public void visit(FieldDeclNode fieldDeclaration) {
		VarDeclListNode v = fieldDeclaration.getDeclaredVariables();
		v.childrenAccept(this);
	}
	
	@Override
	public void visit(LocalVarDeclStmtNode l) {
		VarDeclListNode vds = l.getVarDeclaration().getDeclarators();
		int size = vds.size();
		for(int i=0;i<size;i++){
			VarDeclNode vd = vds.elementAt(i);
			vd.accept(this);
		}
	}
	
	@Override
	public void visit(VarAssignDeclNode variableAssignDecl) {
		invokeExprVisitor(variableAssignDecl.getInitializer(),true);
		
		if(replaceExpression!=null){
			variableAssignDecl.setInitializer(replaceExpression);
			replaceExpression = null;
		}
		//If statements were generated, we need to add those to the init code of the variable decl
		if(!insertBeforeStmts.isEmpty()){
			VarDecl decl = variableAssignDecl.getName().getSemantics();
			decl.addInitCode(insertBeforeStmts);
			insertBeforeStmts.clear();
		}

	}
	
	@Override
	public void visit(AccessorDeclNode accessorDeclaration) {
		accessorDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(StaticInitDeclNode s){
		s.childrenAccept(this);
		
		//flush implicit locals
		varProvider.flushMethodBufferToFunction((Function) s.getSemantics());
	}
	
	@Override
	public void visit(MethodDeclNode methodDeclaration) {		
		//Get function body, if this function has none (abstract/interface) we don't have to do anything
		StmtNode body = methodDeclaration.getBody();
		if(body == null) return;
		
		methodDeclaration.childrenAccept(this);
		
		//flush implicit locals
		varProvider.flushMethodBufferToFunction((Function) methodDeclaration.getSemantics());
	}
	
	@Override
	public void visit(WhileStmtNode whileStatement) {
		//Do condition
		ExprNode cond = whileStatement.getCondition();
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
		StmtNode s = whileStatement.getThenStatement();
		s.accept(this);
		
		popBeforeContinue();
	}
	
	@Override
	public void visit(ForEachStmtNode forEachStatement) {
	
		
		//Nothing has to be inserted before a continue
		pushBeforeContinue();
		
		//Do body
		StmtNode s = forEachStatement.getThenStatement();
		s.accept(this);
		
		//Do the init expression, we do it after the body so generated statements get prepended to the top statement list
		ExprNode cond = forEachStatement.getExpression();
		invokeExprVisitor(cond);
		if(replaceExpression!=null){
			forEachStatement.setCondition(replaceExpression);
			replaceExpression = null;
		}	
		
		popBeforeContinue();
	}
	
	@Override
	public void visit(IfStmtNode s) {
		
		//then and else
		StmtNode b = s.getThenStatement();
		b.accept(this);
		b = s.getElseStatement();
		if(b != null) b.accept(this);

		//Do condition
		ExprNode cond = s.getCondition();
		invokeExprVisitor(cond);
		if(replaceExpression!=null){
			s.setCondition(replaceExpression);
			replaceExpression = null;
		}		
		
		//Insert before condition statements are just passed to the next level,
		//so do nothing here
	
	}
	
	@Override
	public void visit(DoWhileStmtNode doWhileStatement) {
		//Do condition
		ExprNode cond = doWhileStatement.getCondition();
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
			StmtListNode body = doWhileStatement.getThenStatement().getStatements();
			insertStatementsAt(body,body.size());
						
		} else {
			//Nothing to add before continue
			pushBeforeContinue();
		}
		
		//Do body
		StmtNode s = doWhileStatement.getThenStatement();
		s.accept(this);
		popBeforeContinue();
	}

	@Override
	public void visit(ForStmtNode forStatement) {
		//Do condition
		ExprNode cond = forStatement.getCondition();
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
		StmtNode s = forStatement.getForInit();
		
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
	public void visit(BlockStmtNode blockStatement) {
		blockStatement.childrenAccept(this);
	}
	
	@Override
	public void visit(StmtListNode statementList) {
		int size = statementList.size();
		for(int i=0;i<size;i++){
			StmtNode s = statementList.elementAt(i);
			s.accept(this);
			
			//Do statement replacement if desired
			if(replaceStatement != null){
				statementList.setElementAt(replaceStatement, i);
				replaceStatement = null;
			}
			
			//Do before insertion if desired
			if(!insertBeforeStmts.isEmpty()){
				for(StmtNode insert: insertBeforeStmts){
					statementList.insertElementAt(insert,i++);
					size++;
				}
				insertBeforeStmts.clear();
			}
			
		}
	}
	
	@Override
	public void visit(ContinueStmtNode continueStatement) {
		//Add statements from the stack before this continue statement
		insertBeforeStmts.addAll(insertBeforeContinue.get(insertBeforeContinue.size()-1));
	}
	
	@Override
	public void visit(ReturnStmtNode returnStatement) {
		ExprNode result = returnStatement.getResult();
		if(result != null){
			invokeExprVisitor(result);
			if(replaceExpression!=null){
				returnStatement.setResult(replaceExpression);
				replaceExpression = null;
			}
		}
	}
	
	@Override
	public void visit(ExprStmtNode e){
		invokeExprVisitor(e.getExpression());
		if(replaceExpression!=null){
			e.setExpression(replaceExpression);
			replaceExpression = null;
		}
	}

	//XPilot: added
	@Override
	public void visit(ExplicitConsCallStmtNode explicitConstructorInvocationStatement) {
		//invokeExprVisitor(explicitConstructorInvocationStatement.getExpression());
		exprVisitor.invokeSelf(explicitConstructorInvocationStatement.getExpression());
		exprVisitor.invokeSelf(explicitConstructorInvocationStatement.getArguments());
		//explicitConstructorInvocationStatement.accept(exprVisitor);
	}
}
