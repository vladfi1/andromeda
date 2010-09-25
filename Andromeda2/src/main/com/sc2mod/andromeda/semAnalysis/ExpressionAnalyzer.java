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

import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.PointerType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.BinaryExpression;
import com.sc2mod.andromeda.syntaxNodes.BinaryOperator;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.UnaryExpression;
import com.sc2mod.andromeda.syntaxNodes.UnaryOperator;

public class ExpressionAnalyzer {

	private ConstantResolveVisitor constResolve;
	private TypeProvider typeProvider;
	
	public ExpressionAnalyzer(ConstantResolveVisitor constResolver, TypeProvider typeProvider){
		this.typeProvider = typeProvider;
		constResolve = constResolver;
	}
	
	public void analyze(BinaryExpression binaryExpression){
		
		//The type of a binop is related to the operator
		int op = binaryExpression.getOperator();
		Expression lExpr = binaryExpression.getLeftExpression();
		Expression rExpr = binaryExpression.getRightExpression();
		//System.out.println(binaryExpression);
		
		Type left = lExpr.getInferedType();
		Type right = rExpr.getInferedType();
		
		if(left == SpecialType.VOID) 
			throw Problem.ofType(ProblemId.BINOP_ON_VOID).at(lExpr)
					.raiseUnrecoverable();
		if(right == SpecialType.VOID)
			throw Problem.ofType(ProblemId.BINOP_ON_VOID).at(rExpr)
					.raiseUnrecoverable();
		
		//If we do checks with null, we need to cast the null to the other type (if possible)
		if(left == SpecialType.NULL){
			left = right;
			lExpr.setInferedType(right);
		} else if(right == SpecialType.NULL){
			right = left;
			rExpr.setInferedType(left);
		}
		
		Type lBase = left.getReachableBaseType();
		Type rBase = right.getReachableBaseType();
		
		
		switch (op) {
		case BinaryOperator.OROR:
		case BinaryOperator.ANDAND:
			//Bool op: both operands must be bool
			if(lBase != BasicType.BOOL) 
				throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(lExpr)
					.details("left","boolean","type bool",left)
					.raiseUnrecoverable();
					
			if(rBase != BasicType.BOOL) 
				throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(rExpr)
				.details("right","boolean","type bool",right)
				.raiseUnrecoverable();
			binaryExpression.setInferedType(left.getCommonSupertype(right));
			binaryExpression.setLeftExpectedType(BasicType.BOOL);
			binaryExpression.setRightExpectedType(BasicType.BOOL);
			break;
		case BinaryOperator.XOR:
			//XOR can be bool op and int op
			//Bool op: both operands must be bool
			if(lBase == BasicType.BOOL){
				 if(rBase != BasicType.BOOL) 
					 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(rExpr)
						.details("right","boolean XOR","type bool",right)
						.raiseUnrecoverable();
				 binaryExpression.setLeftExpectedType(BasicType.BOOL);
				 binaryExpression.setRightExpectedType(BasicType.BOOL);
			}else if(lBase.isIntegerType()){
				 if(!rBase.isIntegerType()) 
					 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(rExpr)
						.details("right","integer XOR","an integer type",right)
						.raiseUnrecoverable();
				 binaryExpression.setLeftExpectedType(lBase);
				 binaryExpression.setRightExpectedType(rBase);
			} else{
				throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(lExpr)
					.details("left","XOR","type bool or an integer type",left)
					.raiseUnrecoverable();
			}
			binaryExpression.setInferedType(left.getCommonSupertype(right));
			break;
		case BinaryOperator.PLUS:
			//Plus can be string concat or text concat
			if(lBase == BasicType.TEXT || rBase == BasicType.TEXT){
				if(lBase ==BasicType.TEXT && rBase == BasicType.TEXT){
					binaryExpression.setInferedType(left.getCommonSupertype(right));
				} else if(lBase == BasicType.TEXT){
					if(!right.canConcatenateCastTo(BasicType.TEXT))
						throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_BINOP_OPERANDS).at(binaryExpression)
							.details("+",left,right)
							.raiseUnrecoverable();
					binaryExpression.setInferedType(left);
				} else {
					if(!left.canConcatenateCastTo(BasicType.TEXT)) 
						throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_BINOP_OPERANDS).at(binaryExpression)
						.details("+",left,right)
						.raiseUnrecoverable();
					binaryExpression.setInferedType(right);
				}
				
				binaryExpression.setLeftExpectedType(BasicType.TEXT);
				binaryExpression.setRightExpectedType(BasicType.TEXT);
				break;				
			}
			
			if(lBase == BasicType.STRING || rBase == BasicType.STRING){
				if(lBase ==BasicType.STRING && rBase == BasicType.STRING){
					//The inferred type is the common super type
					binaryExpression.setInferedType(left.getCommonSupertype(right));
				} else if(lBase == BasicType.STRING){
					if(!right.canConcatenateCastTo(BasicType.STRING)) 
						throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_BINOP_OPERANDS).at(binaryExpression)
						.details("+",left,right)
						.raiseUnrecoverable();
					binaryExpression.setInferedType(left); //Inferred type is the left one
				} else {
					if(!left.canConcatenateCastTo(BasicType.STRING))
						throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_BINOP_OPERANDS).at(binaryExpression)
						.details("+",left,right)
						.raiseUnrecoverable();
					binaryExpression.setInferedType(right); //Inferred type is the right one
				}
				
				binaryExpression.setLeftExpectedType(BasicType.STRING);
				binaryExpression.setRightExpectedType(BasicType.STRING);
				break;				
			}
		case BinaryOperator.MINUS:
		case BinaryOperator.DIV:
		case BinaryOperator.MULT:
		case BinaryOperator.MOD:
			//Numeric operations
			if(!lBase.isIntegerType() && lBase != BasicType.FLOAT) 
				 if(rBase != BasicType.BOOL) 
					 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(lExpr)
						.details("left","arithmetic binary","a numeric type",left)
						.raiseUnrecoverable();
			if(!rBase.isIntegerType() && rBase != BasicType.FLOAT)
				 if(rBase != BasicType.BOOL) 
					 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(rExpr)
						.details("right","arithmetic binary","a numeric type",right)
						.raiseUnrecoverable();
			
			//If one of both operands is float, then the result is float, else int
			if(lBase == BasicType.FLOAT||rBase == BasicType.FLOAT){
				binaryExpression.setLeftExpectedType(BasicType.FLOAT);
				binaryExpression.setRightExpectedType(BasicType.FLOAT);
			} else {
				binaryExpression.setLeftExpectedType(lBase);
				binaryExpression.setRightExpectedType(rBase);
			}
			binaryExpression.setInferedType(left.getCommonSupertype(right));
			break;
		case BinaryOperator.EQEQ:
		case BinaryOperator.NOTEQ:
			boolean error = false;
			binaryExpression.setInferedType(BasicType.BOOL);
			
			//Equalty and not equalty, can compare many things...
			if(lBase == rBase){
				if(lBase == BasicType.BOOL){
					binaryExpression.setInferedType(left.getCommonSupertype(right));
				}
				//Equalty of two values with the same (basic) type			
			} else if(left == SpecialType.NULL||right == SpecialType.NULL){
				//Reference equalty on basic types with a null pointer
				if((!left.canBeNull())) 
					if(error) 
						throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_COMPARISON).at(lExpr)
								.details(left,SpecialType.NULL)
								.raiseUnrecoverable();
				if((!right.canBeNull())) 
					throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_COMPARISON).at(rExpr)
					.details(right,SpecialType.NULL)
					.raiseUnrecoverable();
			} else if(lBase == BasicType.INT||lBase == BasicType.FLOAT){
				//Numeric equalty
				if(rBase != BasicType.INT&&rBase != BasicType.FLOAT) error = true;
			} else if(left.getCategory() == Type.CLASS||right.getCategory() == Type.CLASS){
				//Reference equalty
				if(!(left.getCategory() == Type.CLASS)||!(right.getCategory() == Type.CLASS))error = true;
				//Error if hierarchy isn't shared
				if(!Class.isHierarchyShared((Class)left,(Class) right)) error = true;
			} else if(lBase == BasicType.BOOL){
				//Boolean equalty
				if(rBase != BasicType.BOOL) error = true;
				binaryExpression.setInferedType(left.getCommonSupertype(right));
			} else {
				error = true;
			}
			if(error) 
				throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_COMPARISON).at(binaryExpression)
				.details(left,right)
				.raiseUnrecoverable();
			break;
		case BinaryOperator.LT:
		case BinaryOperator.GT:
		case BinaryOperator.LTEQ:
		case BinaryOperator.GTEQ:
			//Numeric compare
			if(!lBase.isIntegerType()&&lBase != BasicType.FLOAT) 
				 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(lExpr)
					.details("left","comparison","a numeric type",left)
					.raiseUnrecoverable();
			if(!rBase.isIntegerType()&&rBase != BasicType.FLOAT)
				 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(rExpr)
					.details("right","comparsion","a numeric type",right)
					.raiseUnrecoverable();
			binaryExpression.setInferedType(BasicType.BOOL);
			break;
		case BinaryOperator.AND:
		case BinaryOperator.OR:
		case BinaryOperator.LSHIFT:
		case BinaryOperator.RSHIFT:
			//Bitwise stuff
			//(No boolean and and or implemented yet!)
			if(!lBase.isIntegerType())
				 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(lExpr)
					.details("left","bitwise binary","an integer type",left)
					.raiseUnrecoverable();
			if(!rBase.isIntegerType()) 
				 throw Problem.ofType(ProblemId.TYPE_ERROR_BINOP_OPERAND_TYPE).at(rExpr)
					.details("right","bitwise binary","an integer type",right)
					.raiseUnrecoverable();
			binaryExpression.setInferedType(left.getCommonSupertype(right));
			break;
		case BinaryOperator.URSHIFT:
			throw new InternalProgramError(binaryExpression,"Unsigned rightshift is not possible in galaxy :(");
		default:
			throw new InternalProgramError(binaryExpression,"Unknown binary operator!");
		}
		
		//If both operands are constant, the expression is constant
		if(lExpr.getConstant()&&rExpr.getConstant()){
			binaryExpression.setConstant(true);
			binaryExpression.accept(constResolve);
		}
	}

	public void analyze(UnaryExpression unaryExpression) {
		Type type = unaryExpression.getExpression().getInferedType();
		int op = unaryExpression.getOperator();
		Expression expr = unaryExpression.getExpression();
		if(type == SpecialType.VOID) 
			throw Problem.ofType(ProblemId.UNOP_ON_VOID).at(unaryExpression)
				.raiseUnrecoverable();
			
		Type base = type.getBaseType();
		
		switch (op) {
		case UnaryOperator.POSTMINUSMINUS:
		case UnaryOperator.PREMINUSMINUS:
		case UnaryOperator.POSTPLUSPLUS:
		case UnaryOperator.PREPLUSPLUS:
			if(!expr.getLValue()){
				throw Problem.ofType(ProblemId.INCDEC_ON_NONLVALUE).at(unaryExpression)
						.raiseUnrecoverable();
			}
			if(expr.getConstant()){
				throw Problem.ofType(ProblemId.INCDEC_ON_CONST).at(unaryExpression)
						.raiseUnrecoverable();
			}
		case UnaryOperator.MINUS:
			if(base == BasicType.INT){
				unaryExpression.setInferedType(type);
				break;
			} 
			if(base == BasicType.FLOAT){
				unaryExpression.setInferedType(type);
				break;
			}
			throw Problem.ofType(ProblemId.TYPE_ERROR_UNOP_OPERAND_TYPE).at(unaryExpression)
					.details("negation","a numeric type",type)
					.raiseUnrecoverable();
		case UnaryOperator.COMP:			
			if(base == BasicType.INT){
				unaryExpression.setInferedType(type);
				break;
			} 
			throw Problem.ofType(ProblemId.TYPE_ERROR_UNOP_OPERAND_TYPE).at(unaryExpression)
					.details("bitwise complement","an integer type",type)
					.raiseUnrecoverable();
		case UnaryOperator.NOT:
			//Not can be a boolean not or a test for "not null"
			if(base == BasicType.BOOL|| type.canBeNull()){
				if(base == BasicType.BOOL)
					unaryExpression.setInferedType(type);
				else
					unaryExpression.setInferedType(BasicType.BOOL);
				break;
			} 
			throw Problem.ofType(ProblemId.TYPE_ERROR_UNOP_OPERAND_TYPE).at(unaryExpression)
				.details("boolean negation","an integer type",type)
				.raiseUnrecoverable();
		case UnaryOperator.DEREFERENCE:
			if(type.getCategory() != Type.POINTER) 
				throw Problem.ofType(ProblemId.TYPE_ERROR_UNOP_OPERAND_TYPE).at(unaryExpression)
					.details("dereference operator","a pointer type",type)
					.raiseUnrecoverable();
			unaryExpression.setInferedType(((PointerType)type).pointsTo());
			unaryExpression.setSimple(expr.getSimple());
			break;
		case UnaryOperator.ADDRESSOF:
			if(!expr.getLValue()) 
				throw Problem.ofType(ProblemId.TYPE_ERROR_UNOP_OPERAND_TYPE).at(unaryExpression)
					.details("address-of operator","a valid lValue",type)
					.raiseUnrecoverable();
			unaryExpression.setInferedType(typeProvider.getPointerType(type));
			break;
		default:
			throw new InternalProgramError(unaryExpression,"UnknownOperand");
		
		}
		
		//If the operand is constant, the expression is constant
		if(expr.getConstant()){
			unaryExpression.setConstant(true);
			unaryExpression.accept(constResolve);
		}
	}
}
