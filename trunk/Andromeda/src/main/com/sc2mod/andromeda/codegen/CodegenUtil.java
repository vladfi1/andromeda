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
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.BinOpSE;
import com.sc2mod.andromeda.syntaxNodes.UnOpSE;

public final class CodegenUtil {

	/**
	 * Utility class.
	 */
	private CodegenUtil(){}

	private static HashMap<IType,HashMap<IType,StringPair>> typeCasts = new HashMap<IType,HashMap<IType,StringPair>>();
	static{
		HashMap<IType,StringPair> cur;
		//Casts from fixed
		typeCasts.put(BasicType.FLOAT, cur = new HashMap<IType, StringPair>());
		cur.put(BasicType.INT, new StringPair("FixedToInt(",")"));
		cur.put(BasicType.BYTE, new StringPair("(FixedToInt(",")&0xff)"));
		cur.put(BasicType.STRING, new StringPair("FixedToString(",",3)"));
		cur.put(BasicType.TEXT, new StringPair("FixedToText(",",3)"));
		cur.put(BasicType.BOOL, new StringPair("(","!=0.0)"));
		
		//Casts from int
		typeCasts.put(BasicType.INT, cur = new HashMap<IType, StringPair>());
		cur.put(BasicType.BYTE, new StringPair("((",")&0xff)"));
		cur.put(BasicType.FLOAT, new StringPair("IntToFixed(",")"));
		cur.put(BasicType.STRING, new StringPair("IntToString(",")"));
		cur.put(BasicType.TEXT, new StringPair("IntToText(",")"));
		cur.put(BasicType.BOOL, new StringPair("(","!=0)"));
		
		//Casts from string
		typeCasts.put(BasicType.STRING, cur = new HashMap<IType, StringPair>());
		cur.put(BasicType.FLOAT, new StringPair("StringToFixed(",")"));
		cur.put(BasicType.INT, new StringPair("StringToInt(",")"));
		cur.put(BasicType.BYTE, new StringPair("(StringToInt(",")&0xff)"));
		cur.put(BasicType.TEXT, new StringPair("StringToText(",")"));
		cur.put(BasicType.BOOL, new StringPair("(","!=null)"));
		
		//Casts from text
		typeCasts.put(BasicType.TEXT, cur = new HashMap<IType, StringPair>());
		cur.put(BasicType.FLOAT, new StringPair("StringToFixed(TextToString(","))"));
		cur.put(BasicType.INT, new StringPair("StringToInt(TextToString(","))"));
		cur.put(BasicType.STRING, new StringPair("TextToString(",")"));
		cur.put(BasicType.BOOL, new StringPair("(","!=null)"));
		
		//XPilot: Casts from bool?
	}
	
	public static StringPair getCastOp(IType fromType, IType toType){
		HashMap<IType, StringPair> tcs = typeCasts.get(fromType);
		if(tcs==null) return null;
		return tcs.get(toType);
	}
	
	
	
	public static String getAssignOp(AssignOpSE type, boolean whitespaces){
		if(whitespaces){
			switch(type){
			case EQ: return " = ";
			case MULTEQ: return " *= ";
			case DIVEQ: return " /= ";
			case MODEQ: return " %= ";
			case PLUSEQ: return " += ";
			case MINUSEQ: return " -= ";
			case LSHIFTEQ: return " <<= ";
			case RSHIFTEQ: return " >>= ";
			case URSHIFTEQ: return " >>>= ";
			case ANDEQ: return " &= ";
			case XOREQ: return " ^= ";
			case OREQ: return " |= ";		
			default: throw new InternalProgramError("Unkonwn assignment operator type: " + type);
			}
		}
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
	

	
	public static String getBinaryOp(BinOpSE type, boolean whitespaces){
		if(whitespaces){
			switch(type){
			case OROR: return " || ";
			case ANDAND: return " && ";
			case OR: return " | ";
			case AND: return " & ";
			case XOR: return " ^ ";
			case EQEQ: return " == ";
			case NOTEQ: return " != ";
			case GT: return " > ";
			case LT: return " < ";
			case GTEQ: return " >= ";
			case LTEQ: return " <= ";
			case LSHIFT: return " << ";
			case RSHIFT: return " >> ";
			case URSHIFT: return " >>> ";
			case PLUS: return " + ";
			case MINUS: return " - ";
			case MULT: return " * ";
			case DIV: return " / ";
			case MOD: return " % ";	
			default: throw new InternalProgramError("Unkonwn binary operator type: " + type);
			}
		}
	
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
