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
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.semAnalysis.NameResolver;
import com.sc2mod.andromeda.semAnalysis.SimplicityDecider;
import com.sc2mod.andromeda.syntaxNodes.AccessTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.BreakStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.IfStmtNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpTypeSE;
import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.FixedObject;
import com.sc2mod.andromeda.vm.data.IntObject;

/**
 * Class for conveniently generating parts of the syntax. 
 * @author J. 'gex' Finis
 *
 */
public class SyntaxGenerator {
	
	public static final ExprListNode EMPTY_EXPRESSIONS = new ExprListNode();

	private Configuration options;
	private NameResolver nameResolver;
	public SyntaxGenerator(Configuration options, NameResolver nameResolver) {
		this.options = options;
		this.nameResolver = nameResolver;
	}


	ExprNode createAccessorGet(AccessorDecl ad, int invocationType, ExprNode lValuePrefix, String funcName){
		int invType;
		if(ad.isStatic()){
			invType = Invocation.TYPE_STATIC;
		} else {
			invType = Invocation.TYPE_METHOD;
		}
		Invocation i = new Invocation(ad.getGetter(), invType);		
		ExprListNode arguments = EMPTY_EXPRESSIONS;
		MethodInvocationExprNode m = new MethodInvocationExprNode(invocationType,lValuePrefix,funcName,arguments,false);
		m.setSemantics(i);	
		m.setInferedType(ad.getType());
		return m;
		
	}
	
	ExprNode createAccessorSetExpr(AccessorDecl ad, int invocationType, ExprNode lValuePrefix, String funcName, ExprNode setTo){
		int invType;
		if(ad.isStatic()){
			invType = Invocation.TYPE_STATIC;
		} else {
			invType = Invocation.TYPE_METHOD;
		}
		Invocation i = new Invocation(ad.getSetter(), invType);		
		ExprListNode arguments = new ExprListNode(setTo);
		MethodInvocationExprNode m = new MethodInvocationExprNode(invocationType,lValuePrefix,funcName,arguments,false);
		m.setSemantics(i);	
		m.setInferedType(SpecialType.VOID);
		return m;
	}
	
	StmtNode createAccessorSetStmt(AccessorDecl ad, int invocationType, ExprNode lValuePrefix, String funcName, ExprNode setTo){
		return genExpressionStatement(createAccessorSetExpr(ad,invocationType,lValuePrefix,funcName,setTo));
	}
	
	ExprNode genFieldAccess(ExprNode prefix,int accessType, String name, Type inferedType, VarDecl semantics){
		FieldAccessExprNode f = new FieldAccessExprNode(prefix, accessType, name);
		f.setInferedType(inferedType);
		f.setSemantics(semantics);
		return f;
	}
	
	ExprNode genBinaryExpression(ExprNode left, ExprNode right, int binOp, Type result, Type leftExpect, Type rightExpect){
		BinOpExprNode binary = new BinOpExprNode(left, right, binOp);
		binary.setInferedType(result);
		binary.setLeftExpectedType(leftExpect);
		binary.setRightExpectedType(rightExpect);
		return binary;
	}
	
	StmtNode genExpressionStatement(ExprNode e){
		return new ExprStmtNode(e);
	}
	
	AssignmentExprNode genAssignExpr(ExprNode leftSide, ExprNode rightSide, int operator, int assignmentType){
		AssignmentExprNode a = new AssignmentExprNode(assignmentType, leftSide, operator, rightSide);
		a.setInferedType(rightSide.getInferedType());
		return a;
	}
	
	StmtNode genAssignStatement(ExprNode leftSide, ExprNode rightSide, int operator, int assignmentType){
		return genExpressionStatement(genAssignExpr(leftSide, rightSide, operator, assignmentType));
	}
	
	ExprNode genDereferExpr(ExprNode e){
		UnOpExprNode derefer = new UnOpExprNode(e,UnOpTypeSE.DEREFERENCE);
		derefer.setInferedType(e.getInferedType().getWrappedType());
		derefer.setSimple(e.getSimple());
		return derefer;
	}
	
	ExprNode genParenthesisExpression(ExprNode e){
		ParenthesisExprNode p = new ParenthesisExprNode(e);
		System.out.println(e);
		p.setInferedType(e.getInferedType());
		p.setSimple(e.getSimple());
		return p;
	}
	
	ExprNode genAddressOfExpr(ExprNode e){
		UnOpExprNode derefer = new UnOpExprNode(e,UnOpTypeSE.ADDRESSOF);
		derefer.setInferedType(e.getInferedType().getWrappedType());
		return derefer;
	}

	public FieldAccessExprNode genSimpleName(String string, VarDecl semantics) {
		FieldAccessExprNode sn = new FieldAccessExprNode(null,AccessTypeSE.SIMPLE,string);
		sn.setSemantics(semantics);
		sn.setSimple(SimplicityDecider.isSimple(sn));
		return sn;
	}

	public LiteralExprNode genIntLiteralExpr(int i) {
		LiteralNode l = new LiteralNode(new IntObject(i),LiteralTypeSE.INT);
		LiteralExprNode le = new LiteralExprNode(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setSimple(true);
		le.setInferedType(BasicType.INT);
		return le;
	}
	
	public LiteralExprNode genFixedLiteralExpr(float f) {
		LiteralNode l = new LiteralNode(new FixedObject(f),LiteralTypeSE.FLOAT);
		LiteralExprNode le = new LiteralExprNode(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setSimple(true);
		le.setInferedType(BasicType.FLOAT);
		return le;
	}

	public IfStmtNode createLoopAbortIf(ExprNode condition) {
		//Negate condition
		ExprNode newCondition;
		if(condition instanceof UnOpExprNode){
			UnOpExprNode u = (UnOpExprNode)condition;
			if(u.getOperator() == UnOpTypeSE.NOT){
				newCondition = u.getExpression();
			} else {
				newCondition = new UnOpExprNode(new ParenthesisExprNode(condition), UnOpTypeSE.NOT);
			}			
		} else newCondition = new UnOpExprNode(new ParenthesisExprNode(condition), UnOpTypeSE.NOT);
		
		//Infer constant value if the expression was constant
		if(condition.getConstant()){
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
		le.setSimple(true);
		le.setInferedType(BasicType.BOOL);
		return le;
	}


	public ExprNode genArrayAccess(ExprNode prefix, ExprNode arrayIndex,
			Type inferedType, VarDecl semantics) {
		ArrayAccessExprNode sn = new ArrayAccessExprNode(prefix,arrayIndex);
		sn.setSemantics(semantics);
		sn.setInferedType(inferedType);
		return sn;
	}


	public StmtNode genDeleteStatement(ExprNode expression) {
		DeleteStmtNode s = new DeleteStmtNode(expression);
		Invocation in = nameResolver.registerDelete(((Class)expression.getInferedType()),s);
		s.setSemantics(in);	
		return s;
	}
}
