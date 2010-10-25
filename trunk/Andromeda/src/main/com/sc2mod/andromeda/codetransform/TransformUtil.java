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

import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.BinOpSE;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;

/**
 * Utility class for code transformations
 * @author J. 'gex' Finis
 *
 */
public final class TransformUtil {

	/**
	 * Utility class
	 */
	private TransformUtil(){}
	
	
	public static BinOpSE getOpFromAssignOp(AssignOpSE operator){
		switch(operator){
		case EQ:	return null;
		case ANDEQ: return BinOpSE.AND;
		case DIVEQ: return BinOpSE.DIV;
		case LSHIFTEQ: return BinOpSE.LSHIFT;
		case MINUSEQ: return BinOpSE.MINUS;
		case MODEQ: return BinOpSE.MOD;
		case MULTEQ: return BinOpSE.MULT;
		case OREQ: return BinOpSE.OR;
		case PLUSEQ: return BinOpSE.PLUS;
		case RSHIFTEQ: return BinOpSE.RSHIFT;
		case URSHIFTEQ: return BinOpSE.URSHIFT;
		case XOREQ: return BinOpSE.XOR;
		default : throw new Error("Unknown op type");
		}
	}
	
	public static boolean isFieldAccess(ExprNode e){
		return e instanceof FieldAccessExprNode || e instanceof NameExprNode;
	}

}
