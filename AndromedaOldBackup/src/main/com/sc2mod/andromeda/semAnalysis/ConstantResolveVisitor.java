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

import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.VisitorErrorAdapater;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;
import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.Fixed;
import com.sc2mod.andromeda.vm.data.FixedObject;
import com.sc2mod.andromeda.vm.data.IntObject;
import com.sc2mod.andromeda.vm.data.StringObject;

public class ConstantResolveVisitor extends VisitorErrorAdapater{

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
		Type t = keyOfExpression.getInferedType();
		if(!t.isKeyType()){
			throw Problem.ofType(ProblemId.KEYOF_USED_ON_NONKEY).at(keyOfExpression).details(t.getFullName())
				.raiseUnrecoverable();
		}
		keyOfExpression.setValue(t.getNextKey());
		
		
	}
	
	@Override
	public void visit(UnOpExprNode unaryExpression) {	
		Type type = unaryExpression.getExpression().getInferedType();
		int op = unaryExpression.getOperator();
		ExprNode expr = unaryExpression.getExpression();
		DataObject val = expr.getValue();
		if(val == null){
			addToResolveList(unaryExpression);
			return;
		}
		resolveCount++;
				
		switch (op) {
		case UnOpTypeSE.POSTMINUSMINUS:
		case UnOpTypeSE.PREMINUSMINUS:
		case UnOpTypeSE.POSTPLUSPLUS:
		case UnOpTypeSE.PREPLUSPLUS:
			break;
		case UnOpTypeSE.MINUS:
			if(type == BasicType.INT){
				int i = val.getIntValue();
				unaryExpression.setValue(new IntObject(-i));
				break;
			} 
			if(type == BasicType.FLOAT){
				Fixed f = val.getFixedValue();
				unaryExpression.setValue(new FixedObject(f.negate()));
				break;
			}
			throw new Error("!");
		case UnOpTypeSE.COMP:	
		{
			int i = val.getIntValue();
			unaryExpression.setValue(new IntObject(~i));
			break;				
		}	
		case UnOpTypeSE.NOT:
			//Not can be a boolean not or a test for "not null"
			if(type == BasicType.BOOL){
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
	
	@Override
	public void visit(FieldAccessExprNode nameExpression) {
		VarDecl v = (VarDecl) nameExpression.getSemantics();
		DataObject d = v.getValue();
		if(d == null){
			addToResolveList(nameExpression);
		} else {
			nameExpression.setValue(d);
			resolveCount++;
		}
	}
	
	@Override
	public void visit(VarAssignDeclNode variableAssignDecl) {
		VarDecl vd = (VarDecl) variableAssignDecl.getSemantics();
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
		
		Type resultType = binaryExpression.getInferedType();
		
		//The type of a binop is related to the operator
		int op = binaryExpression.getOperator();
		ExprNode lExpr = binaryExpression.getLeftExpression();
		ExprNode rExpr = binaryExpression.getRightExpression();
		Type left = lExpr.getInferedType().getBaseType();
		Type right = rExpr.getInferedType().getBaseType();
		DataObject lVal = lExpr.getValue();
		DataObject rVal = rExpr.getValue();
		

		if(lVal == null || rVal == null){
			addToResolveList(binaryExpression);
			return;
		}
		resolveCount++;
		
		switch (op) {
		case BinOpTypeSE.OROR:
		case BinOpTypeSE.ANDAND:
		{
			boolean b1 = lVal.getBoolValue();
			boolean b2 = rVal.getBoolValue();
			boolean result;
			switch (op) {
			case BinOpTypeSE.OROR:	result = b1 || b2; break;
			case BinOpTypeSE.ANDAND:	result = b1 && b2; break;
			default:					throw new Error("!");
			}
			binaryExpression.setValue(BoolObject.getBool(result));
			break;
		}
		case BinOpTypeSE.XOR: 
		{
			if(left == BasicType.BOOL){
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
		case BinOpTypeSE.PLUS:
		{
			if(resultType == BasicType.TEXT|| resultType == BasicType.STRING){
				//XPilot: replaced getStringValue() with toString()
				String s1 = lVal.toString();
				String s2 = rVal.toString();
				binaryExpression.setValue(new StringObject(s1 + s2));
				break;
			}
		}
		case BinOpTypeSE.MINUS:
		case BinOpTypeSE.DIV:
		case BinOpTypeSE.MULT:
		case BinOpTypeSE.MOD:
		{
			//If one of both operands is float, then the result is float, else int
			if(resultType == BasicType.FLOAT){
				Fixed f1 = lVal.getFixedValue();
				Fixed f2 = rVal.getFixedValue();
				Fixed result;
				switch(op){
				case BinOpTypeSE.PLUS:	result = Fixed.sum(f1, f2); break;
				case BinOpTypeSE.MINUS:	result = Fixed.difference(f1, f2); break;
				case BinOpTypeSE.DIV:	result = Fixed.quotient(f1, f2); break;
				case BinOpTypeSE.MULT:	result = Fixed.product(f1, f2); break;
				case BinOpTypeSE.MOD:	result = Fixed.modulus(f1, f2); break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(new FixedObject(result));
			} else {
				int i1 = lVal.getIntValue();
				int i2 = rVal.getIntValue();
				int result;
				switch(op){
				case BinOpTypeSE.PLUS:	result = i1 + i2; break;
				case BinOpTypeSE.MINUS:	result = i1 - i2; break;
				case BinOpTypeSE.DIV:	result = i1 / i2; break;
				case BinOpTypeSE.MULT:	result = i1 * i2; break;
				case BinOpTypeSE.MOD:	result = i1 % i2; break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(new IntObject(result));	
			}
			break;
		}
		case BinOpTypeSE.EQEQ:
		case BinOpTypeSE.NOTEQ:
			if(left == BasicType.BOOL&&right == BasicType.BOOL){
				boolean b1 = lVal.getBoolValue();
				boolean f2 = rVal.getBoolValue();
				boolean result;
				switch(op){
				case BinOpTypeSE.EQEQ:	result = b1 == f2; break;
				case BinOpTypeSE.NOTEQ:	result = b1 != f2; break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(BoolObject.getBool(result));
				break;
			}
		case BinOpTypeSE.LT:
		case BinOpTypeSE.GT:
		case BinOpTypeSE.LTEQ:
		case BinOpTypeSE.GTEQ:
			if(left == BasicType.FLOAT||right == BasicType.FLOAT){
				Fixed f1 = lVal.getFixedValue();
				Fixed f2 = rVal.getFixedValue();
				boolean result;
				switch(op){
				case BinOpTypeSE.EQEQ:	result = Fixed.equal(f1, f2); break;
				case BinOpTypeSE.NOTEQ:	result = Fixed.notEqual(f1, f2); break;
				case BinOpTypeSE.LT:		result = Fixed.lessThan(f1, f2); break;
				case BinOpTypeSE.GT:		result = Fixed.greaterThan(f1, f2); break;
				case BinOpTypeSE.LTEQ:	result = Fixed.lessThanOrEqualTo(f1, f2); break;
				case BinOpTypeSE.GTEQ:	result = Fixed.greaterThanOrEqualTo(f1, f2); break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(BoolObject.getBool(result));
			} else {
				int i1 = lVal.getIntValue();
				int i2 = rVal.getIntValue();
				boolean result;
				switch(op){
				case BinOpTypeSE.EQEQ:	result = i1 == i2; break;
				case BinOpTypeSE.NOTEQ:	result = i1 != i2; break;
				case BinOpTypeSE.LT:		result = i1 < i2; break;
				case BinOpTypeSE.GT:		result = i1 > i2; break;
				case BinOpTypeSE.LTEQ:	result = i1 <= i2; break;
				case BinOpTypeSE.GTEQ:	result = i1 >= i2; break;
				default:					throw new Error("!");
				}
				binaryExpression.setValue(BoolObject.getBool(result));
			}
			break;
		case BinOpTypeSE.AND:
		case BinOpTypeSE.OR:
		case BinOpTypeSE.LSHIFT:
		case BinOpTypeSE.RSHIFT:
		case BinOpTypeSE.URSHIFT:
			{
				int i1 = lVal.getIntValue();
				int i2 = rVal.getIntValue();
				int result;
				switch(op){
				case BinOpTypeSE.AND:	result = i1 & i2; break;
				case BinOpTypeSE.OR:		result = i1 | i2; break;
				case BinOpTypeSE.LSHIFT:	result = i1 << i2; break;
				case BinOpTypeSE.RSHIFT:	result = i1 >> i2; break;
				case BinOpTypeSE.URSHIFT:result = i1 >>> i2; break;
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
