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

import com.sc2mod.andromeda.syntaxNodes.AssignOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.BinOpTypeSE;

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
		case AssignOpTypeSE.EQ:	return -1;
		case AssignOpTypeSE.ANDEQ: return BinOpTypeSE.AND;
		case AssignOpTypeSE.DIVEQ: return BinOpTypeSE.DIV;
		case AssignOpTypeSE.LSHIFTEQ: return BinOpTypeSE.LSHIFT;
		case AssignOpTypeSE.MINUSEQ: return BinOpTypeSE.MINUS;
		case AssignOpTypeSE.MODEQ: return BinOpTypeSE.MOD;
		case AssignOpTypeSE.MULTEQ: return BinOpTypeSE.MULT;
		case AssignOpTypeSE.OREQ: return BinOpTypeSE.OR;
		case AssignOpTypeSE.PLUSEQ: return BinOpTypeSE.PLUS;
		case AssignOpTypeSE.RSHIFTEQ: return BinOpTypeSE.RSHIFT;
		case AssignOpTypeSE.URSHIFTEQ: return BinOpTypeSE.URSHIFT;
		case AssignOpTypeSE.XOREQ: return BinOpTypeSE.XOR;
		default : throw new Error("Unknown op type");
		}
	}
	

}
