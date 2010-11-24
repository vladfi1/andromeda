/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;

import java.util.LinkedList;
import java.util.ListIterator;

import com.sc2mod.andromeda.environment.access.AccessUtil;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpSE;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpSE;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;
import com.sc2mod.andromeda.util.visitors.VoidVisitorErrorAdapater;
import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.Fixed;
import com.sc2mod.andromeda.vm.data.FixedObject;
import com.sc2mod.andromeda.vm.data.IntObject;
import com.sc2mod.andromeda.vm.data.StringObject;

public class ConstantResolveVisitor extends VoidVisitorErrorAdapater{

	public ConstantResolveVisitor() {
		// TODO Auto-generated constructor stub
	}
	
	private int resolveCount = 0;
	private boolean resolveRemaining;
	
	private LinkedList<ExprNode> expressionsToResolve = new LinkedList<ExprNode>();
	private LinkedList<VarAssignDeclNode> declsToResolve = new LinkedList<VarAssignDeclNode>();
	private void addToResolveList(ExprNode e) {
		if(resolveRemaining) return;
		expressionsToResolve.add(e);		
	}

	private void addToResolveList(VarAssignDeclNode vd) {
		if(resolveRemaining) return;
		declsToResolve.add(vd);
	}
	
	public void resolveRemainingExprs(){
		resolveRemaining = true;
		int resolveCount = this.resolveCount;
		int resolveCountBefore = -1;
		
		//As long as something happens try again
		while(resolveCountBefore < resolveCount){
			resolveCountBefore = resolveCount;
			
			ListIterator<ExprNode> li = expressionsToResolve.listIterator();
			while(li.hasNext()){
				ExprNode e = li.next();
				e.accept(this);
				
				//Resolve successful!
				if(this.resolveCount != resolveCount){
					li.remove();
					resolveCount = this.resolveCount;
				}
			}
			
			ListIterator<VarAssignDeclNode> li2 = declsToResolve.listIterator();
			while(li2.hasNext()){
				VarAssignDeclNode e = li2.next();
				e.accept(this);
				
				//Resolve successful!
				if(this.resolveCount != resolveCount){
					li2.remove();
					resolveCount = this.resolveCount;
				}
			}
		}
	}

	
	@Override
	public void visit(LiteralExprNode literalExpression) {
		literalExpression.setValue(literalExpression.getLiteral().getValue());
		resolveCount++;
	}
	
	@Override
	public void visit(KeyOfExprNode keyOfExpression) {
		IType t = keyOfExpression.getInferedType();
		if(!t.isKeyType()){
			throw Problem.ofType(ProblemId.KEYOF_USED_ON_NONKEY).at(keyOfExpression).details(t.getFullName())
				.raiseUnrecoverable();
		}
		keyOfExpression.setValue(t.getNextKey());
		
		
	}
	
	@Override
	public void visit(UnOpExprNode unaryExpression) {	
		IType type = unaryExpression.getExpression().getInferedType();
		UnOpSE op = unaryExpression.getUnOp();
		ExprNode expr = unaryExpression.getExpression();
		DataObject val = expr.getValue();
		if(val == null){
			addToResolveList(unaryExpression);
			return;
		}
		resolveCount++;
		
		BasicTypeSet BASIC = type.getTypeProvider().BASIC;
		
				
		switch (op) {
		case POSTMINUSMINUS:
		case PREMINUSMINUS:
		case POSTPLUSPLUS:
		case PREPLUSPLUS:
			break;
		case MINUS:
			if(type == BASIC.INT){
				int i = val.getIntValue();
				unaryExpression.setValue(new IntObject(-i));
				break;
			} 
			if(type == BASIC.FLOAT){
				Fixed f = val.getFixedValue();
				unaryExpression.setValue(new FixedObject(f.negate()));
				break;
			}
			throw new Error("!");
		case COMP:	
		{
			int i = val.getIntValue();
			unaryExpression.setValue(new IntObject(~i));
			break;				
		}	
		case NOT:
			//Not can be a boolean not or a test for "not null"
			if(type == BASIC.BOOL){
				boolean i = val.getBoolValue();
				unaryExpression.setValue(BoolObject.getBool(!i));
				break;	
			} else if(type.canBeNull()){
				boolean i = val.isNull();
				unaryExpression.setValue(BoolObject.getBool(!i));
				break;	
			}
			throw new Error("!");
		default:
			throw new InternalError("UnknownOperand");
		
		}

	}
	
	private void resolveName(ExprNode nameExpression) {
		Variable v = AccessUtil.getVarIfVarAccess((NameAccess) nameExpression.getSemantics());
		if(v == null){
			return;
		}
		DataObject d = v.getValue();
		if(d == null){
			addToResolveList(nameExpression);
		} else {
			nameExpression.setValue(d);
			resolveCount++;
		}
	}
	
	@Override
	public void visit(FieldAccessExprNode nameExpression) {
		resolveName(nameExpression);
	}


	@Override
	public void visit(NameExprNode nameExprNode) {
		resolveName(nameExprNode);
	}
	
	@Override
	public void visit(VarAssignDeclNode variableAssignDecl) {
		Variable vd = variableAssignDecl.getName().getSemantics();
		DataObject d = variableAssignDecl.getInitializer().getValue();
		if(d == null){
			addToResolveList(variableAssignDecl);
		} else {
			vd.setValue(d.castTo(vd.getType()));
			resolveCount++;
		}
	}
	

	
	@Override
	public void visit(CastExprNode castExpression) {
		DataObject val = castExpression.getRightExpression().getValue();
		if(val == null){
			addToResolveList(castExpression);
			return;
		}
		
		castExpression.setValue(val.castTo(castExpression.getInferedType()));
	}

	@Override
	public void visit(BinOpExprNode binaryExpression) {
		
		IType resultType = binaryExpression.getInferedType();
		
		//The type of a binop is related to the operator
		BinOpSE op = binaryExpression.getBinOp();
		ExprNode lExpr = binaryExpression.getLeftExpression();
		ExprNode rExpr = binaryExpression.getRightExpression();
		IType left = lExpr.getInferedType().getBaseType();
		IType right = rExpr.getInferedType().getBaseType();
		DataObject lVal = lExpr.getValue();
		DataObject rVal = rExpr.getValue();

		BasicTypeSet BASIC = left.getTypeProvider().BASIC;

		if(lVal == null || rVal == null){
			addToResolveList(binaryExpression);
			return;
		}
		resolveCount++;
		
		switch (op) {
		case OROR:
		case ANDAND:
		{
			boolean b1 = lVal.getBoolValue();
			boolean b2 = rVal.getBoolValue();
			boolean result;
			switch (op) {
			case OROR:	result = b1 || b2; break;
			case ANDAND:	result = b1 && b2; break;
			default:					throw new Error("!");
			}
			binaryExpression.setValue(BoolObject.getBool(result));
			break;
		}
		case XOR: 
		{
			if(left == BASIC.BOOL){
				boolean b1 = lVal.getBoolValue();
				boolean b2 = rVal.getBoolValue();
				boolean result;
				result = b1 ^ b2;
				binaryExpression.setValue(BoolObject.getBool(result));
				break;
			} else {
				int b1 = lVal.getIntValue();
				int b2 = rVal.getIntValue();
				int result;
				result = b1 ^ b2;
				binaryExpression.setValue(new IntObject(result));
				break;
			}
		
		}
		case PLUS:
		{
			if(resultType == BASIC.TEXT|| resultType == BASIC.STRING){
				//XPilot: replaced getStringValue() with toString()
				String s1 = lVal.toString();
				String s2 = rVal.toString();
				binaryExpression.setValue(new StringObject(s1 + s2));
				break;
			}
		}
		case MINUS:
		case DIV:
		case MULT:
		case MOD:
		{
			//If one of both operands is float, then the result is float, else int
			if(resultType == BASIC.FLOAT){
				Fixed f1 = lVal.getFixedValue();
				Fixed f2 = rVal.getFixedValue();
				Fixed result;
				switch(op){
				case PLUS:	result = Fixed.sum(f1, f2); break;
				case MINUS:	result = Fixed.difference(f1, f2); break;
				case DIV:	result = Fixed.quotient(f1, f2); break;
				case MULT:	result = Fixed.product(f1, f2); break;
				case MOD:	result = Fixed.modulus(f1, f2); break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(new FixedObject(result));
			} else {
				int i1 = lVal.getIntValue();
				int i2 = rVal.getIntValue();
				int result;
				switch(op){
				case PLUS:	result = i1 + i2; break;
				case MINUS:	result = i1 - i2; break;
				case DIV:	result = i1 / i2; break;
				case MULT:	result = i1 * i2; break;
				case MOD:	result = i1 % i2; break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(new IntObject(result));	
			}
			break;
		}
		case EQEQ:
		case NOTEQ:
			if(left == BASIC.BOOL&&right == BASIC.BOOL){
				boolean b1 = lVal.getBoolValue();
				boolean f2 = rVal.getBoolValue();
				boolean result;
				switch(op){
				case EQEQ:	result = b1 == f2; break;
				case NOTEQ:	result = b1 != f2; break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(BoolObject.getBool(result));
				break;
			}
		case LT:
		case GT:
		case LTEQ:
		case GTEQ:
			if(left == BASIC.FLOAT||right == BASIC.FLOAT){
				Fixed f1 = lVal.getFixedValue();
				Fixed f2 = rVal.getFixedValue();
				boolean result;
				switch(op){
				case EQEQ:	result = Fixed.equal(f1, f2); break;
				case NOTEQ:	result = Fixed.notEqual(f1, f2); break;
				case LT:		result = Fixed.lessThan(f1, f2); break;
				case GT:		result = Fixed.greaterThan(f1, f2); break;
				case LTEQ:	result = Fixed.lessThanOrEqualTo(f1, f2); break;
				case GTEQ:	result = Fixed.greaterThanOrEqualTo(f1, f2); break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(BoolObject.getBool(result));
			} else {
				int i1 = lVal.getIntValue();
				int i2 = rVal.getIntValue();
				boolean result;
				switch(op){
				case EQEQ:	result = i1 == i2; break;
				case NOTEQ:	result = i1 != i2; break;
				case LT:		result = i1 < i2; break;
				case GT:		result = i1 > i2; break;
				case LTEQ:	result = i1 <= i2; break;
				case GTEQ:	result = i1 >= i2; break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(BoolObject.getBool(result));
			}
			break;
		case AND:
		case OR:
		case LSHIFT:
		case RSHIFT:
		case URSHIFT:
			{
				int i1 = lVal.getIntValue();
				int i2 = rVal.getIntValue();
				int result;
				switch(op){
				case AND:	result = i1 & i2; break;
				case OR:		result = i1 | i2; break;
				case LSHIFT:	result = i1 << i2; break;
				case RSHIFT:	result = i1 >> i2; break;
				case URSHIFT:result = i1 >>> i2; break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(new IntObject(result));
				
			}
			break;
		default:
			throw new InternalError("Unknown binary operator!");
		}
		
		
	}
}
