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

import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.syntaxNodes.AccessTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.AssignmentTypeSE;
import com.sc2mod.andromeda.syntaxNodes.BinOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpTypeSE;

/**
 * Does the 'ugly' expression transformations for 
 * increment, decrement and assignments inside expressions.
 * Does various optimizations which makes the code very ugly.
 * Check the corresponding chart in the doc folder.
 * @author J. 'gex' Finis
 *
 */
public class UglyExprTransformer {
	
	private SyntaxGenerator syntaxGenerator;
	private ImplicitLocalVarProvider varProvider;
	private TypeProvider typeProvider;
	private TransformationVisitor parent;
	private LiteralExprNode intOneExpr;
	private LiteralExprNode fixedOneExpr;
	private ComplexExpressionChopper complexChopper;
	
	public UglyExprTransformer(TransformationVisitor parent,
								TypeProvider typeProvider,
								ImplicitLocalVarProvider var,
								SyntaxGenerator syntaxGenerator) {
		this.parent = parent;
		this.typeProvider = typeProvider;
		this.varProvider = var;
		this.syntaxGenerator = syntaxGenerator;
		this.intOneExpr = syntaxGenerator.genIntLiteralExpr(1);
		this.fixedOneExpr = syntaxGenerator.genFixedLiteralExpr(1.f);
		this.complexChopper = new ComplexExpressionChopper(syntaxGenerator, varProvider);
	}

	
	public ExprNode transform(UnOpExprNode u, boolean inExpr){
		ExprNode expr = u.getExpression();
		
		//Choose operators
		int op = u.getOperator();
		int assignOp;
		int compensateOp = -1;
		switch(op){
		case UnOpTypeSE.COMP:
		case UnOpTypeSE.MINUS:
		case UnOpTypeSE.NOT:
		case UnOpTypeSE.ADDRESSOF:
		case UnOpTypeSE.DEREFERENCE:
			//No need to transform these
			return null;
		case UnOpTypeSE.PREPLUSPLUS:
			assignOp = AssignOpTypeSE.PLUSEQ;
			break;
		case UnOpTypeSE.PREMINUSMINUS:
			assignOp = AssignOpTypeSE.MINUSEQ;
			break;
		case UnOpTypeSE.POSTPLUSPLUS:
			assignOp = AssignOpTypeSE.PLUSEQ;
			compensateOp = BinOpTypeSE.MINUS;
			break;
		case UnOpTypeSE.POSTMINUSMINUS:
			assignOp = AssignOpTypeSE.MINUSEQ;
			compensateOp = BinOpTypeSE.PLUS;
			break;
		default: throw new InternalError("Unkonwn unary operator type: " + op);
		}
					
		//Choose literal
		ExprNode literal;
		if(expr.getInferedType()==BasicType.FLOAT){
			literal = fixedOneExpr;
		} else {
			literal = intOneExpr;
		}
		
		//Choose assignment type
		int at = AssignmentTypeSE.FIELD;
		if(expr instanceof UnOpExprNode){
			at = AssignmentTypeSE.POINTER;
		} else if(expr instanceof ArrayAccessExprNode){
			at = AssignmentTypeSE.ARRAY;
		}
		
		AssignmentExprNode assignment = syntaxGenerator.genAssignExpr(u.getExpression(), literal, assignOp, at);
		if(compensateOp == -1){
			return transformUglyInternal(assignment, inExpr, -1, null);
		} else {
			return transformUglyInternal(assignment, inExpr, compensateOp, literal);
		}
	}
	
	public ExprNode transform(AssignmentExprNode a, boolean inExpr){
		return transformUglyInternal(a,inExpr,0,null);
	}
	
	private ExprNode surroundWithBinaryIncDecReplacement(ExprNode toSurround, int binOp, ExprNode incDecExpr){
		if(incDecExpr == null){
			//No inc dec adjustment necessary?
			return toSurround;
		}
		Type t = toSurround.getInferedType();
		
		ExprNode binExpr = syntaxGenerator.genBinaryExpression(toSurround, incDecExpr, binOp, t, t, incDecExpr.getInferedType());
		return syntaxGenerator.genParenthesisExpression(binExpr);
	}
	
	private ExprNode transformUglyInternal(AssignmentExprNode a, boolean inExpr, int binOp, ExprNode incDecExpr){
		ExprNode leftSide = a.getLeftExpression();
		ExprNode rightSide = a.getRightExpression();
		
		ExprNode replace = null;
		int assignType = a.getAssignmentType();
		VarDecl vd = null;
		ExprNode prefix = null;
		String accessorName = null;
		boolean isPrefixSimple = false;
		int accessorInvType = 0;
		switch(assignType){
		case AssignmentTypeSE.FIELD:
			vd = (VarDecl) leftSide.getSemantics();
			if(vd.isAccessor()){
				if(!vd.isStatic())
					prefix = leftSide.getLeftExpression();
				accessorName = leftSide.getName();
				isPrefixSimple = prefix==null?true:prefix.getSimple();
				accessorInvType = prefix==null?AccessTypeSE.SIMPLE:AccessTypeSE.EXPRESSION;
			}
			break;
		case AssignmentTypeSE.ARRAY:
			vd = (VarDecl) leftSide.getSemantics();
			if(vd.isAccessor()){
				throw new Error("Cannot have array accessors");
			}
			break;
		case AssignmentTypeSE.POINTER:
			break;
		default:
			throw new Error("Not supported!");
		}
		int operator = a.getOperator();
		boolean isAccessor = vd==null?false:vd.isAccessor();
		boolean isLeftSimple = leftSide.getSimple();
		boolean isRightSimple = rightSide.getSimple();
		boolean isCalc = operator!=AssignOpTypeSE.EQ;
		FieldAccessExprNode z;
		
		if(inExpr){
			if(isAccessor){
				if(isPrefixSimple){
					if(isCalc){
						//System.out.println("CASE 6b");
						//(6b)
						//z = geta(this)+c
						//seta(this, z)		
						//R: z
						z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
						ExprNode get = syntaxGenerator.createAccessorGet((AccessorDecl) vd, accessorInvType, prefix, accessorName);
						int op = TransformUtil.getOpFromAssignOp(operator);
						ExprNode binary = syntaxGenerator.genBinaryExpression(get,rightSide,op,get.getInferedType(),get.getInferedType(),get.getInferedType());
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, binary, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));						
						parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorDecl) vd, accessorInvType, prefix, accessorName,z));
						replace = surroundWithBinaryIncDecReplacement(z,binOp,incDecExpr);
					} else {
						if(isRightSimple){

							//System.out.println("CASE 5");
							//(5)
							//seta(this, c )
							//R: c
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorDecl) vd, accessorInvType, prefix, accessorName,rightSide));
							replace = rightSide;
						} else {
							//System.out.println("CASE 6");
							//(6)
							//z = Y
							//seta(this, z)
							//R: z
							z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, rightSide, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorDecl) vd, accessorInvType, prefix, accessorName,z));
							replace = z;
						}
					}
				} else {
					if(isCalc){
						//System.out.println("CASE 9");
						//(9)
						//z = X
						//z2 = geta(z) + c
						//seta(z,z2)
						//R: z2
						z = varProvider.getImplicitLocalVar(typeProvider.getPointerType(prefix.getInferedType()));
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, prefix, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
						ExprNode get = syntaxGenerator.createAccessorGet((AccessorDecl) vd, accessorInvType, z, accessorName);
						int op = TransformUtil.getOpFromAssignOp(operator);
						ExprNode binary = syntaxGenerator.genBinaryExpression(get,rightSide,op,get.getInferedType(),get.getInferedType(),get.getInferedType());
						ExprNode z2 = varProvider.getImplicitLocalVar(leftSide.getInferedType());
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z2, binary, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
						parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorDecl) vd, accessorInvType, z, accessorName,z2));
						replace = surroundWithBinaryIncDecReplacement(z2,binOp,incDecExpr);
					} else {
						if(isRightSimple){
							//System.out.println("CASE 7");
							//(7)
							//seta(X, c)
							//R: c
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorDecl) vd, accessorInvType, prefix, accessorName,rightSide));
							replace = rightSide;
						} else {
							//System.out.println("CASE 8");
							//(8)
							//z = Y
							//seta(X, z)
							//R: z
							z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, rightSide, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorDecl) vd, accessorInvType, prefix, accessorName,z));
							replace = z;
						}
					}
				}
			} else {				
				if(isLeftSimple){
					//System.out.println("CASE 1");
					//(1)
					parent.addStatementBefore(syntaxGenerator.genExpressionStatement(a));
					replace = surroundWithBinaryIncDecReplacement(leftSide,binOp,incDecExpr);
				} else {
					if(isCalc){
						//System.out.println("CASE 4");
						//(4)
						//P = chop(X)
						//P += c
						//R: P
						//Old with pointers:
//						z = varProvider.getImplicitLocalVar(typeProvider.getPointerType(leftSide.getInferedType()));
//						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, 
//										syntaxGenerator.genAddressOfExpr(leftSide),
//										AssignmentOperatorType.EQ, AssignmentType.FIELD));
//						Expression dereferExpr = syntaxGenerator.genDereferExpr(z);
//						parent.addStatementBefore(syntaxGenerator.genAssignStatement(dereferExpr,
//										rightSide, operator, AssignmentType.POINTER));
//						replace = surroundWithBinaryIncDecReplacement(dereferExpr,binOp,incDecExpr);
						
						ExprNode left = complexChopper.chop(leftSide,parent);
						//z = varProvider.getImplicitLocalVar(typeProvider.getPointerType(leftSide.getInferedType()));
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(left, 
										rightSide,
										operator, AssignmentTypeSE.FIELD));
						replace = surroundWithBinaryIncDecReplacement(left,binOp,incDecExpr);						
					} else {
						if(isRightSimple){
							//System.out.println("CASE 2");
							//(2)
							parent.addStatementBefore(syntaxGenerator.genExpressionStatement(a));
							replace = rightSide;
						} else {
							//System.out.println("CASE 3");
							//(3)
							z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, rightSide, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(leftSide, z, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
							replace = z;
						}
					}
				}
			}		
		} else {
			if(isAccessor){
				if(isCalc){
					//(9) simple
					//z = X
					//R: seta(z,geta(z)+Y)
					z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
					parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, prefix, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
					ExprNode get = syntaxGenerator.createAccessorGet((AccessorDecl) vd, accessorInvType, z, accessorName);
					int op = TransformUtil.getOpFromAssignOp(operator);
					ExprNode binary = syntaxGenerator.genBinaryExpression(get,rightSide,op,get.getInferedType(),get.getInferedType(),get.getInferedType());
					replace = syntaxGenerator.createAccessorSetExpr((AccessorDecl) vd, accessorInvType, z, accessorName,binary);
				} else {
					//(5+6+7+8) simple
					//R: seta(X, Y)	
					replace = syntaxGenerator.createAccessorSetExpr((AccessorDecl) vd, accessorInvType, prefix, accessorName,rightSide);
				}
				
			} else {
				return a;
			}
		}
				
		return replace;
	}
}
