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
import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccess;
import com.sc2mod.andromeda.syntaxNodes.Assignment;
import com.sc2mod.andromeda.syntaxNodes.BinaryExpression;
import com.sc2mod.andromeda.syntaxNodes.BlockStatement;
import com.sc2mod.andromeda.syntaxNodes.BreakStatement;
import com.sc2mod.andromeda.syntaxNodes.DeleteStatement;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.ExpressionStatement;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.IfThenElseStatement;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocation;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExpression;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.StatementList;
import com.sc2mod.andromeda.syntaxNodes.UnaryExpression;
import com.sc2mod.andromeda.syntaxNodes.UnaryOperator;
import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.FixedObject;
import com.sc2mod.andromeda.vm.data.IntObject;

/**
 * Class for conveniently generating parts of the syntax. 
 * @author J. 'gex' Finis
 *
 */
public class SyntaxGenerator {
	
	public static final ExpressionList EMPTY_EXPRESSIONS = new ExpressionList();

	private Configuration options;
	private NameResolver nameResolver;
	public SyntaxGenerator(Configuration options, NameResolver nameResolver) {
		this.options = options;
		this.nameResolver = nameResolver;
	}


	Expression createAccessorGet(AccessorDecl ad, int invocationType, Expression lValuePrefix, String funcName){
		int invType;
		if(ad.isStatic()){
			invType = Invocation.TYPE_STATIC;
		} else {
			invType = Invocation.TYPE_METHOD;
		}
		Invocation i = new Invocation(ad.getGetter(), invType);		
		ExpressionList arguments = EMPTY_EXPRESSIONS;
		MethodInvocation m = new MethodInvocation(invocationType,lValuePrefix,funcName,arguments,false);
		m.setSemantics(i);	
		m.setInferedType(ad.getType());
		return m;
		
	}
	
	Expression createAccessorSetExpr(AccessorDecl ad, int invocationType, Expression lValuePrefix, String funcName, Expression setTo){
		int invType;
		if(ad.isStatic()){
			invType = Invocation.TYPE_STATIC;
		} else {
			invType = Invocation.TYPE_METHOD;
		}
		Invocation i = new Invocation(ad.getSetter(), invType);		
		ExpressionList arguments = new ExpressionList(setTo);
		MethodInvocation m = new MethodInvocation(invocationType,lValuePrefix,funcName,arguments,false);
		m.setSemantics(i);	
		m.setInferedType(SpecialType.VOID);
		return m;
	}
	
	Statement createAccessorSetStmt(AccessorDecl ad, int invocationType, Expression lValuePrefix, String funcName, Expression setTo){
		return genExpressionStatement(createAccessorSetExpr(ad,invocationType,lValuePrefix,funcName,setTo));
	}
	
	Expression genFieldAccess(Expression prefix,int accessType, String name, Type inferedType, VarDecl semantics){
		FieldAccess f = new FieldAccess(prefix, accessType, name);
		f.setInferedType(inferedType);
		f.setSemantics(semantics);
		return f;
	}
	
	Expression genBinaryExpression(Expression left, Expression right, int binOp, Type result, Type leftExpect, Type rightExpect){
		BinaryExpression binary = new BinaryExpression(left, right, binOp);
		binary.setInferedType(result);
		binary.setLeftExpectedType(leftExpect);
		binary.setRightExpectedType(rightExpect);
		return binary;
	}
	
	Statement genExpressionStatement(Expression e){
		return new ExpressionStatement(e);
	}
	
	Assignment genAssignExpr(Expression leftSide, Expression rightSide, int operator, int assignmentType){
		Assignment a = new Assignment(assignmentType, leftSide, operator, rightSide);
		a.setInferedType(rightSide.getInferedType());
		return a;
	}
	
	Statement genAssignStatement(Expression leftSide, Expression rightSide, int operator, int assignmentType){
		return genExpressionStatement(genAssignExpr(leftSide, rightSide, operator, assignmentType));
	}
	
	Expression genDereferExpr(Expression e){
		UnaryExpression derefer = new UnaryExpression(e,UnaryOperator.DEREFERENCE);
		derefer.setInferedType(e.getInferedType().getWrappedType());
		derefer.setSimple(e.getSimple());
		return derefer;
	}
	
	Expression genParenthesisExpression(Expression e){
		ParenthesisExpression p = new ParenthesisExpression(e);
		System.out.println(e);
		p.setInferedType(e.getInferedType());
		p.setSimple(e.getSimple());
		return p;
	}
	
	Expression genAddressOfExpr(Expression e){
		UnaryExpression derefer = new UnaryExpression(e,UnaryOperator.ADDRESSOF);
		derefer.setInferedType(e.getInferedType().getWrappedType());
		return derefer;
	}

	public FieldAccess genSimpleName(String string, VarDecl semantics) {
		FieldAccess sn = new FieldAccess(null,AccessType.SIMPLE,string);
		sn.setSemantics(semantics);
		sn.setSimple(SimplicityDecider.isSimple(sn));
		return sn;
	}

	public LiteralExpression genIntLiteralExpr(int i) {
		Literal l = new Literal(new IntObject(i),LiteralType.INT);
		LiteralExpression le = new LiteralExpression(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setSimple(true);
		le.setInferedType(BasicType.INT);
		return le;
	}
	
	public LiteralExpression genFixedLiteralExpr(float f) {
		Literal l = new Literal(new FixedObject(f),LiteralType.FLOAT);
		LiteralExpression le = new LiteralExpression(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setSimple(true);
		le.setInferedType(BasicType.FLOAT);
		return le;
	}

	public IfThenElseStatement createLoopAbortIf(Expression condition) {
		//Negate condition
		Expression newCondition;
		if(condition instanceof UnaryExpression){
			UnaryExpression u = (UnaryExpression)condition;
			if(u.getOperator() == UnaryOperator.NOT){
				newCondition = u.getExpression();
			} else {
				newCondition = new UnaryExpression(new ParenthesisExpression(condition), UnaryOperator.NOT);
			}			
		} else newCondition = new UnaryExpression(new ParenthesisExpression(condition), UnaryOperator.NOT);
		
		//Infer constant value if the expression was constant
		if(condition.getConstant()){
			newCondition.setConstant(true);
			if(condition.getValue()!=null){
				newCondition.setValue(BoolObject.getBool(condition.getValue().getBoolValue()));
			}
		}
		
		//Create the if block
		return new IfThenElseStatement(newCondition, new BlockStatement(new StatementList(new BreakStatement(null))), null);
		
	}

	public Expression genBoolLiteralExpr(boolean b) {
		Literal l = new Literal(BoolObject.getBool(b),LiteralType.BOOL);
		LiteralExpression le = new LiteralExpression(l);
		le.setConstant(true);
		le.setValue(l.getValue());
		le.setSimple(true);
		le.setInferedType(BasicType.BOOL);
		return le;
	}


	public Expression genArrayAccess(Expression prefix, Expression arrayIndex,
			Type inferedType, VarDecl semantics) {
		ArrayAccess sn = new ArrayAccess(prefix,arrayIndex);
		sn.setSemantics(semantics);
		sn.setInferedType(inferedType);
		return sn;
	}


	public Statement genDeleteStatement(Expression expression) {
		DeleteStatement s = new DeleteStatement(expression);
		Invocation in = nameResolver.registerDelete(((Class)expression.getInferedType()),s);
		s.setSemantics(in);	
		return s;
	}
}
