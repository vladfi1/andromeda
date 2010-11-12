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

import com.sc2mod.andromeda.environment.access.AccessorAccess;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.access.InvocationType;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.types.basic.SpecialType;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.semAnalysis.LoopSemantics;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpSE;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.BreakStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.IfStmtNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.TypeNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpSE;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;
import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.FixedObject;
import com.sc2mod.andromeda.vm.data.IntObject;

/**
 * Class for conveniently generating parts of the syntax. 
 * @author J. 'gex' Finis
 *
 */
public class SyntaxGenerator {
	
	private BasicTypeSet BASIC;

	public SyntaxGenerator(TypeProvider tp){
		this.BASIC = tp.BASIC;
	}
	
	public static final ExprListNode EMPTY_EXPRESSIONS = new ExprListNode();

	ExprNode createMethodInvocation(ExprNode prefix, String funcName, ExprListNode arguments, Invocation semantics){
		MethodInvocationExprNode expr = new MethodInvocationExprNode(prefix, funcName, arguments, null);
		expr.setSemantics(semantics);
		expr.setInferedType(semantics.getReturnType());
		return expr;
	}
	
	ExprNode createAccessorGet(AccessorAccess ad, ExprNode lValuePrefix, String funcName){
		InvocationType invType;
		if(ad.isStatic()){
			invType = InvocationType.STATIC;
		} else {
			//TODO: Virtual calls for accessors
			invType = InvocationType.METHOD;
		}
		Invocation i = new Invocation(ad.getGetMethod(), invType);		
		ExprListNode arguments = EMPTY_EXPRESSIONS;
		MethodInvocationExprNode m = new MethodInvocationExprNode(lValuePrefix,funcName,arguments,null);
		m.setSemantics(i);	
		m.setInferedType(ad.getAccessedElement().getReturnType());
		return m;
		
	}
	
	ExprNode createAccessorSetExpr(AccessorAccess ad, ExprNode lValuePrefix, String funcName, ExprNode setTo){
		InvocationType invType;
		if(ad.isStatic()){
			invType = InvocationType.STATIC;
		} else {
			//TODO: Virtual calls for accessors
			invType = InvocationType.METHOD;
		}
		Invocation i = new Invocation(ad.getSetMethod(), invType);		
		ExprListNode arguments = new ExprListNode(setTo);
		MethodInvocationExprNode m = new MethodInvocationExprNode(lValuePrefix,funcName,arguments,null);
		m.setSemantics(i);	
		m.setInferedType(BASIC.VOID);
		return m;
	}
	
	StmtNode createAccessorSetStmt(AccessorAccess ad, ExprNode lValuePrefix, String funcName, ExprNode setTo){
		return genExpressionStatement(createAccessorSetExpr(ad,lValuePrefix,funcName,setTo));
	}
	
	ExprNode genFieldAccess(ExprNode prefix, String name, IType inferedType, NameAccess vd){
		FieldAccessExprNode f = new FieldAccessExprNode(prefix, name);
		f.setInferedType(inferedType);
		f.setSemantics(vd);
		return f;
	}
	
	ExprNode genBinaryExpression(ExprNode left, ExprNode right, BinOpSE binOp, IType result, IType leftExpect, IType rightExpect){
		BinOpExprNode binary = new BinOpExprNode(left, right, binOp);
		binary.setInferedType(result);
		binary.setLeftExpectedType(leftExpect);
		binary.setRightExpectedType(rightExpect);
		return binary;
	}
	
	StmtNode genExpressionStatement(ExprNode e){
		return new ExprStmtNode(e);
	}
	
	AssignmentExprNode genAssignExpr(ExprNode leftSide, ExprNode rightSide, AssignOpSE operator){
		AssignmentExprNode a = new AssignmentExprNode(leftSide, operator, rightSide);
		a.setInferedType(rightSide.getInferedType());
		return a;
	}
	
	StmtNode genAssignStatement(ExprNode leftSide, ExprNode rightSide, AssignOpSE operator){
		return genExpressionStatement(genAssignExpr(leftSide, rightSide, operator));
	}
	
	ExprNode genDereferExpr(ExprNode e){
		UnOpExprNode derefer = new UnOpExprNode(e,UnOpSE.DEREFERENCE);
		derefer.setInferedType(e.getInferedType().getWrappedType());
		return derefer;
	}
	
	ExprNode genParenthesisExpression(ExprNode e){
		ParenthesisExprNode p = new ParenthesisExprNode(e);
		p.setInferedType(e.getInferedType());
		return p;
	}
	
	ExprNode genAddressOfExpr(ExprNode e){
		UnOpExprNode derefer = new UnOpExprNode(e,UnOpSE.ADDRESSOF);
		derefer.setInferedType(e.getInferedType().getWrappedType());
		return derefer;
	}

	public NameExprNode genSimpleName(String string, Variable semantics) {
		NameExprNode sn = new NameExprNode(string);
		sn.setSemantics(semantics);
		return sn;
	}

	public LiteralExprNode genIntLiteralExpr(int i) {
		LiteralNode l = new LiteralNode(new IntObject(i),LiteralTypeSE.INT);
		LiteralExprNode le = new LiteralExprNode(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setInferedType(BASIC.INT);
		return le;
	}
	
	public LiteralExprNode genFixedLiteralExpr(float f) {
		LiteralNode l = new LiteralNode(new FixedObject(f),LiteralTypeSE.FLOAT);
		LiteralExprNode le = new LiteralExprNode(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setInferedType(BASIC.FLOAT);
		return le;
	}

	public IfStmtNode createLoopAbortIf(ExprNode condition) {
		//Negate condition
		ExprNode newCondition;
		if(condition instanceof UnOpExprNode){
			UnOpExprNode u = (UnOpExprNode)condition;
			if(u.getUnOp() == UnOpSE.NOT){
				newCondition = u.getExpression();
			} else {
				newCondition = new UnOpExprNode(new ParenthesisExprNode(condition), UnOpSE.NOT);
			}			
		} else newCondition = new UnOpExprNode(new ParenthesisExprNode(condition), UnOpSE.NOT);
		
		//Infer constant value if the expression was constant
		if(condition.isConstant()){
			newCondition.setConstant(true);
			if(condition.getValue()!=null){
				newCondition.setValue(BoolObject.getBool(condition.getValue().getBoolValue()));
			}
		}
		
		//Create the if block
		return new IfStmtNode(newCondition, new BlockStmtNode(new StmtListNode(new BreakStmtNode(null))), null);
		
	}

	public ExprNode genBoolLiteralExpr(boolean b) {
		LiteralNode l = new LiteralNode(BoolObject.getBool(b),LiteralTypeSE.BOOL);
		LiteralExprNode le = new LiteralExprNode(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setInferedType(BASIC.BOOL);
		return le;
	}


	public ExprNode genArrayAccess(ExprNode prefix, ExprNode arrayIndex,
			IType inferedType, NameAccess semantics) {
		ArrayAccessExprNode sn = new ArrayAccessExprNode(prefix,arrayIndex);
		sn.setSemantics(semantics);
		sn.setInferedType(inferedType);
		return sn;
	}


	public StmtNode genDeleteStatement(ExprNode expression) {
		DeleteStmtNode s = new DeleteStmtNode(expression);
		Invocation in = ResolveUtil.registerDelete(((IClass)expression.getInferedType()),s);
		s.setSemantics(in);	
		return s;
	}

	public StmtNode genLocalVarAssignDeclStmt(TypeNode type, IdentifierNode identifier, ExprNode initializer ) {
		VarAssignDeclNode assignDecl = new VarAssignDeclNode(identifier, initializer);
		VarDeclListNode assignDeclList = new VarDeclListNode(assignDecl);
		LocalVarDeclNode Variable = new LocalVarDeclNode(null, type, assignDeclList);
		return new LocalVarDeclStmtNode(Variable);
		
	}

	public WhileStmtNode createWhileLoop(ExprNode condition, BlockStmtNode body, LoopSemantics loopSemantics) {
		WhileStmtNode loop = new WhileStmtNode(condition, body);
		loop.setSemantics(loopSemantics);
		return loop;
	}
}
