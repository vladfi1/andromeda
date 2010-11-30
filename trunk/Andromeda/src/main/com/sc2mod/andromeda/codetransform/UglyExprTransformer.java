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
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.semAnalysis.SimplicityDecisionVisitor;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpSE;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpSE;

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
	
	private SimplicityDecisionVisitor simpleDecider = new SimplicityDecisionVisitor();
	
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
		this.complexChopper = new ComplexExpressionChopper(syntaxGenerator, varProvider,typeProvider);
	}

	
	public ExprNode transform(UnOpExprNode u, boolean inExpr){
		ExprNode expr = u.getExpression();
		
		//Choose operators
		UnOpSE op = u.getUnOp();
		AssignOpSE assignOp;
		BinOpSE compensateOp = null;
		switch(op){
		case PREPLUSPLUS:
			assignOp = AssignOpSE.PLUSEQ;
			break;
		case PREMINUSMINUS:
			assignOp = AssignOpSE.MINUSEQ;
			break;
		case POSTPLUSPLUS:
			assignOp = AssignOpSE.PLUSEQ;
			compensateOp = BinOpSE.MINUS;
			break;
		case POSTMINUSMINUS:
			assignOp = AssignOpSE.MINUSEQ;
			compensateOp = BinOpSE.PLUS;
			break;
		default:
			//All other operators do not have to be transformed.
			return null;
		}
					
		//Choose literal
		ExprNode literal;
		if(expr.getInferedType()==typeProvider.BASIC.FLOAT){
			literal = fixedOneExpr;
		} else {
			literal = intOneExpr;
		}
		
		AssignmentExprNode assignment = syntaxGenerator.genAssignExpr(u.getExpression(), literal, assignOp);
		if(compensateOp == null){
			return transformUglyInternal(assignment, inExpr, null, null);
		} else {
			return transformUglyInternal(assignment, inExpr, compensateOp, literal);
		}
	}
	
	public ExprNode transform(AssignmentExprNode a, boolean inExpr){
		return transformUglyInternal(a,inExpr,null,null);
	}
	
	private ExprNode surroundWithBinaryIncDecReplacement(ExprNode toSurround, BinOpSE binOp, ExprNode incDecExpr){
		if(incDecExpr == null){
			//No inc dec adjustment necessary?
			return toSurround;
		}
		IType t = toSurround.getInferedType();
		
		ExprNode binExpr = syntaxGenerator.genBinaryExpression(toSurround, incDecExpr, binOp, t, t, incDecExpr.getInferedType());
		return syntaxGenerator.genParenthesisExpression(binExpr);
	}
	
	private ExprNode transformUglyInternal(AssignmentExprNode a, boolean inExpr, BinOpSE binOp, ExprNode incDecExpr){
		ExprNode leftSide = a.getLeftExpression();
		ExprNode rightSide = a.getRightExpression();
		
		ExprNode replace = null;
		//Variable vd = null;
		ExprNode prefix = null;
		String accessorName = null;
		boolean isPrefixSimple = true;
		boolean isAccessor = false;
		NameAccess acc = (NameAccess)leftSide.getSemantics();
		if(TransformUtil.isFieldAccess(leftSide)){
			
			switch(acc.getAccessType()){
			case ACCESSOR:
				isAccessor = true;
				if(leftSide instanceof FieldAccessExprNode){
					prefix = leftSide.getLeftExpression();
					if(!acc.isStatic())
						isPrefixSimple = simpleDecider.isSimple(prefix);
					
				}
				accessorName = leftSide.getName();
			}
		}
	
		AssignOpSE operator = a.getAssignOp();
		boolean isLeftSimple = simpleDecider.isSimple(leftSide);
		boolean isRightSimple = simpleDecider.isSimple(rightSide);
		boolean isCalc = operator!=AssignOpSE.EQ;
		NameExprNode z;
		
		String debugCase;
		
		if(inExpr){
			if(isAccessor){
				if(isPrefixSimple){
					if(isCalc){
						debugCase = "6b";
						//System.out.println("CASE 6b");
						//(6b)
						//z = geta(this)+c
						//seta(this, z)		
						//R: z
						z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
						ExprNode get = syntaxGenerator.createAccessorGet((AccessorAccess) acc, prefix, accessorName);
						BinOpSE op = TransformUtil.getOpFromAssignOp(operator);
						ExprNode binary = syntaxGenerator.genBinaryExpression(get,rightSide,op,get.getInferedType(),get.getInferedType(),get.getInferedType());
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, binary, AssignOpSE.EQ));						
						parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorAccess) acc, prefix, accessorName,z));
						replace = surroundWithBinaryIncDecReplacement(z,binOp,incDecExpr);
					} else {
						if(isRightSimple){
							debugCase = "5";
							//System.out.println("CASE 5");
							//(5)
							//seta(this, c )
							//R: c
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorAccess) acc, prefix, accessorName,rightSide));
							replace = rightSide;
						} else {
							debugCase = "6";
							//System.out.println("CASE 6");
							//(6)
							//z = Y
							//seta(this, z)
							//R: z
							z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, rightSide, AssignOpSE.EQ));
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorAccess) acc, prefix, accessorName,z));
							replace = z;
						}
					}
				} else {
					if(isCalc){
						debugCase = "9";
						//System.out.println("CASE 9");
						//(9)
						//z = X
						//z2 = geta(z) + c
						//seta(z,z2)
						//R: z2
						z = varProvider.getImplicitLocalVar(typeProvider.getPointerType(prefix.getInferedType()));
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, prefix, AssignOpSE.EQ));
						ExprNode get = syntaxGenerator.createAccessorGet((AccessorAccess) acc, z, accessorName);
						BinOpSE op = TransformUtil.getOpFromAssignOp(operator);
						ExprNode binary = syntaxGenerator.genBinaryExpression(get,rightSide,op,get.getInferedType(),get.getInferedType(),get.getInferedType());
						ExprNode z2 = varProvider.getImplicitLocalVar(leftSide.getInferedType());
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z2, binary, AssignOpSE.EQ));
						parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorAccess) acc, z, accessorName,z2));
						replace = surroundWithBinaryIncDecReplacement(z2,binOp,incDecExpr);
					} else {
						if(isRightSimple){
							debugCase = "7";
							//System.out.println("CASE 7");
							//(7)
							//seta(X, c)
							//R: c
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorAccess) acc, prefix, accessorName,rightSide));
							replace = rightSide;
						} else {
							debugCase = "8";
							//System.out.println("CASE 8");
							//(8)
							//z = Y
							//seta(X, z)
							//R: z
							z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, rightSide, AssignOpSE.EQ));
							parent.addStatementBefore(syntaxGenerator.createAccessorSetStmt((AccessorAccess) acc, prefix, accessorName,z));
							replace = z;
						}
					}
				}
			} else {				
				if(isLeftSimple){
					debugCase = "1";
					//System.out.println("CASE 1");
					//(1)
					parent.addStatementBefore(syntaxGenerator.genExpressionStatement(a));
					replace = surroundWithBinaryIncDecReplacement(leftSide,binOp,incDecExpr);
				} else {
					if(isCalc){
						debugCase = "4";
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
										operator));
						replace = surroundWithBinaryIncDecReplacement(left,binOp,incDecExpr);						
					} else {
						if(isRightSimple){
							debugCase = "2";
							//System.out.println("CASE 2");
							//(2)
							parent.addStatementBefore(syntaxGenerator.genExpressionStatement(a));
							replace = rightSide;
						} else {
							debugCase = "3";
							//System.out.println("CASE 3");
							//(3)
							z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, rightSide, AssignOpSE.EQ));
							parent.addStatementBefore(syntaxGenerator.genAssignStatement(leftSide, z, AssignOpSE.EQ));
							replace = z;
						}
					}
				}
			}		
		} else {
			if(isAccessor){
				if(isCalc){
					if(isPrefixSimple){
						debugCase = "a9s";
						//(9) prefix simple
						//R: X.seta(X.geta()+Y)
						ExprNode get = syntaxGenerator.createAccessorGet((AccessorAccess) acc, prefix, accessorName);
						BinOpSE op = TransformUtil.getOpFromAssignOp(operator);
						ExprNode binary = syntaxGenerator.genBinaryExpression(get,rightSide,op,get.getInferedType(),get.getInferedType(),get.getInferedType());
						replace = syntaxGenerator.createAccessorSetExpr((AccessorAccess) acc, prefix, accessorName,binary);

					} else {
						debugCase = "a9";
						//(9) simple
						//z = X
						//R: z.seta(z.geta()+Y)
						z = varProvider.getImplicitLocalVar(leftSide.getInferedType());
						parent.addStatementBefore(syntaxGenerator.genAssignStatement(z, prefix, AssignOpSE.EQ));
						ExprNode get = syntaxGenerator.createAccessorGet((AccessorAccess) acc, z, accessorName);
						BinOpSE op = TransformUtil.getOpFromAssignOp(operator);
						ExprNode binary = syntaxGenerator.genBinaryExpression(get,rightSide,op,get.getInferedType(),get.getInferedType(),get.getInferedType());
						replace = syntaxGenerator.createAccessorSetExpr((AccessorAccess) acc, z, accessorName,binary);	
					}
				} else {
					debugCase = "a5678";
					//(5+6+7+8) simple
					//R: seta(X, Y)	
					replace = syntaxGenerator.createAccessorSetExpr((AccessorAccess) acc, prefix, accessorName,rightSide);
				}
				
			} else {
				return a;
			}
		}		
		return replace;
	}
}
