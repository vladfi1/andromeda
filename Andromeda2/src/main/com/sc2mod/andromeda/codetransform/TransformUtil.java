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

import com.sc2mod.andromeda.syntaxNodes.AssignmentOperatorType;
import com.sc2mod.andromeda.syntaxNodes.BinaryOperator;

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
	
	
	public static int getOpFromAssignOp(int operator){
		switch(operator){
		case AssignmentOperatorType.EQ:	return -1;
		case AssignmentOperatorType.ANDEQ: return BinaryOperator.AND;
		case AssignmentOperatorType.DIVEQ: return BinaryOperator.DIV;
		case AssignmentOperatorType.LSHIFTEQ: return BinaryOperator.LSHIFT;
		case AssignmentOperatorType.MINUSEQ: return BinaryOperator.MINUS;
		case AssignmentOperatorType.MODEQ: return BinaryOperator.MOD;
		case AssignmentOperatorType.MULTEQ: return BinaryOperator.MULT;
		case AssignmentOperatorType.OREQ: return BinaryOperator.OR;
		case AssignmentOperatorType.PLUSEQ: return BinaryOperator.PLUS;
		case AssignmentOperatorType.RSHIFTEQ: return BinaryOperator.RSHIFT;
		case AssignmentOperatorType.URSHIFTEQ: return BinaryOperator.URSHIFT;
		case AssignmentOperatorType.XOREQ: return BinaryOperator.XOR;
		default : throw new Error("Unknown op type");
		}
	}
	

}
