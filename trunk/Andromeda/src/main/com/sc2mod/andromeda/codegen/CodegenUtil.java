/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.BinOpSE;
import com.sc2mod.andromeda.syntaxNodes.UnOpSE;

public final class CodegenUtil {

	/**
	 * Utility class.
	 */
	private CodegenUtil(){}


	
	
	
	public static String getAssignOp(AssignOpSE type){
		switch(type){
		case EQ: return "=";
		case MULTEQ: return "*=";
		case DIVEQ: return "/=";
		case MODEQ: return "%=";
		case PLUSEQ: return "+=";
		case MINUSEQ: return "-=";
		case LSHIFTEQ: return "<<=";
		case RSHIFTEQ: return ">>=";
		case URSHIFTEQ: return ">>>=";
		case ANDEQ: return "&=";
		case XOREQ: return "^=";
		case OREQ: return "|=";
		default: throw new InternalProgramError("Unkonwn assignment operator type: " + type);
		}	
	}
	

	
	public static String getBinaryOp(BinOpSE type){
		switch(type){
		case OROR: return "||";
		case ANDAND: return "&&";
		case OR: return "|";
		case AND: return "&";
		case XOR: return "^";
		case EQEQ: return "==";
		case NOTEQ: return "!=";
		case GT: return ">";
		case LT: return "<";
		case GTEQ: return ">=";
		case LTEQ: return "<=";
		case LSHIFT: return "<<";
		case RSHIFT: return ">>";
		case URSHIFT: return ">>>";
		case PLUS: return "+";
		case MINUS: return "-";
		case MULT: return "*";
		case DIV: return "/";
		case MOD: return "%";	
		default: throw new InternalProgramError("Unkonwn binary operator type: " + type);
		}
	}
	

	
	public static String getUnaryOp(UnOpSE type){
		switch(type){
		case COMP: return "~";
		case MINUS: return "-";
		case NOT: return "!";
		case PREPLUSPLUS: return "++";
		case PREMINUSMINUS: return "--";
		case POSTPLUSPLUS: return "++";
		case POSTMINUSMINUS: return "--";
		case ADDRESSOF: return "&";
		case DEREFERENCE: return "*";
		default: throw new InternalProgramError("Unknown unary operator type: " + type);
		}

	}	
}
