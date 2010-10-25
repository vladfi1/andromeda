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

import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.AssignOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.BinOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.UnOpTypeSE;

public final class CodegenUtil {

	/**
	 * Utility class.
	 */
	private CodegenUtil(){}

	private static HashMap<Type,HashMap<Type,StringPair>> typeCasts = new HashMap<Type,HashMap<Type,StringPair>>();
	static{
		HashMap<Type,StringPair> cur;
		//Casts from fixed
		typeCasts.put(BasicType.FLOAT, cur = new HashMap<Type, StringPair>());
		cur.put(BasicType.INT, new StringPair("FixedToInt(",")"));
		cur.put(BasicType.BYTE, new StringPair("(FixedToInt(",")&0xff)"));
		cur.put(BasicType.STRING, new StringPair("FixedToString(",",3)"));
		cur.put(BasicType.TEXT, new StringPair("FixedToText(",",3)"));
		cur.put(BasicType.BOOL, new StringPair("(","!=0.0)"));
		
		//Casts from int
		typeCasts.put(BasicType.INT, cur = new HashMap<Type, StringPair>());
		cur.put(BasicType.BYTE, new StringPair("((",")&0xff)"));
		cur.put(BasicType.FLOAT, new StringPair("IntToFixed(",")"));
		cur.put(BasicType.STRING, new StringPair("IntToString(",")"));
		cur.put(BasicType.TEXT, new StringPair("IntToText(",")"));
		cur.put(BasicType.BOOL, new StringPair("(","!=0)"));
		
		//Casts from string
		typeCasts.put(BasicType.STRING, cur = new HashMap<Type, StringPair>());
		cur.put(BasicType.FLOAT, new StringPair("StringToFixed(",")"));
		cur.put(BasicType.INT, new StringPair("StringToInt(",")"));
		cur.put(BasicType.BYTE, new StringPair("(StringToInt(",")&0xff)"));
		cur.put(BasicType.TEXT, new StringPair("StringToText(",")"));
		cur.put(BasicType.BOOL, new StringPair("(","!=null)"));
		
		//Casts from text
		typeCasts.put(BasicType.TEXT, cur = new HashMap<Type, StringPair>());
		cur.put(BasicType.FLOAT, new StringPair("StringToFixed(TextToString(","))"));
		cur.put(BasicType.INT, new StringPair("StringToInt(TextToString(","))"));
		cur.put(BasicType.STRING, new StringPair("TextToString(",")"));
		cur.put(BasicType.BOOL, new StringPair("(","!=null)"));
		
		//XPilot: Casts from bool?
	}
	
	public static StringPair getCastOp(Type fromType, Type toType){
		HashMap<Type, StringPair> tcs = typeCasts.get(fromType);
		if(tcs==null) return null;
		return tcs.get(toType);
	}
	
	
	
	public static String getAssignOp(int type, boolean whitespaces){
		if(whitespaces){
			switch(type){
			case AssignOpTypeSE.EQ: return " = ";
			case AssignOpTypeSE.MULTEQ: return " *= ";
			case AssignOpTypeSE.DIVEQ: return " /= ";
			case AssignOpTypeSE.MODEQ: return " %= ";
			case AssignOpTypeSE.PLUSEQ: return " += ";
			case AssignOpTypeSE.MINUSEQ: return " -= ";
			case AssignOpTypeSE.LSHIFTEQ: return " <<= ";
			case AssignOpTypeSE.RSHIFTEQ: return " >>= ";
			case AssignOpTypeSE.URSHIFTEQ: return " >>>= ";
			case AssignOpTypeSE.ANDEQ: return " &= ";
			case AssignOpTypeSE.XOREQ: return " ^= ";
			case AssignOpTypeSE.OREQ: return " |= ";		
			default: throw new InternalProgramError("Unkonwn assignment operator type: " + type);
			}
		}
		switch(type){
		case AssignOpTypeSE.EQ: return "=";
		case AssignOpTypeSE.MULTEQ: return "*=";
		case AssignOpTypeSE.DIVEQ: return "/=";
		case AssignOpTypeSE.MODEQ: return "%=";
		case AssignOpTypeSE.PLUSEQ: return "+=";
		case AssignOpTypeSE.MINUSEQ: return "-=";
		case AssignOpTypeSE.LSHIFTEQ: return "<<=";
		case AssignOpTypeSE.RSHIFTEQ: return ">>=";
		case AssignOpTypeSE.URSHIFTEQ: return ">>>=";
		case AssignOpTypeSE.ANDEQ: return "&=";
		case AssignOpTypeSE.XOREQ: return "^=";
		case AssignOpTypeSE.OREQ: return "|=";
		default: throw new InternalProgramError("Unkonwn assignment operator type: " + type);
		}	
	}
	

	
	public static String getBinaryOp(int type, boolean whitespaces){
		if(whitespaces){
			switch(type){
			case BinOpTypeSE.OROR: return " || ";
			case BinOpTypeSE.ANDAND: return " && ";
			case BinOpTypeSE.OR: return " | ";
			case BinOpTypeSE.AND: return " & ";
			case BinOpTypeSE.XOR: return " ^ ";
			case BinOpTypeSE.EQEQ: return " == ";
			case BinOpTypeSE.NOTEQ: return " != ";
			case BinOpTypeSE.GT: return " > ";
			case BinOpTypeSE.LT: return " < ";
			case BinOpTypeSE.GTEQ: return " >= ";
			case BinOpTypeSE.LTEQ: return " <= ";
			case BinOpTypeSE.LSHIFT: return " << ";
			case BinOpTypeSE.RSHIFT: return " >> ";
			case BinOpTypeSE.URSHIFT: return " >>> ";
			case BinOpTypeSE.PLUS: return " + ";
			case BinOpTypeSE.MINUS: return " - ";
			case BinOpTypeSE.MULT: return " * ";
			case BinOpTypeSE.DIV: return " / ";
			case BinOpTypeSE.MOD: return " % ";	
			default: throw new InternalProgramError("Unkonwn binary operator type: " + type);
			}
		}
	
		switch(type){
		case BinOpTypeSE.OROR: return "||";
		case BinOpTypeSE.ANDAND: return "&&";
		case BinOpTypeSE.OR: return "|";
		case BinOpTypeSE.AND: return "&";
		case BinOpTypeSE.XOR: return "^";
		case BinOpTypeSE.EQEQ: return "==";
		case BinOpTypeSE.NOTEQ: return "!=";
		case BinOpTypeSE.GT: return ">";
		case BinOpTypeSE.LT: return "<";
		case BinOpTypeSE.GTEQ: return ">=";
		case BinOpTypeSE.LTEQ: return "<=";
		case BinOpTypeSE.LSHIFT: return "<<";
		case BinOpTypeSE.RSHIFT: return ">>";
		case BinOpTypeSE.URSHIFT: return ">>>";
		case BinOpTypeSE.PLUS: return "+";
		case BinOpTypeSE.MINUS: return "-";
		case BinOpTypeSE.MULT: return "*";
		case BinOpTypeSE.DIV: return "/";
		case BinOpTypeSE.MOD: return "%";	
		default: throw new InternalProgramError("Unkonwn binary operator type: " + type);
		}
	}
	

	
	public static String getUnaryOp(int type){
		switch(type){
		case UnOpTypeSE.COMP: return "~";
		case UnOpTypeSE.MINUS: return "-";
		case UnOpTypeSE.NOT: return "!";
		case UnOpTypeSE.PREPLUSPLUS: return "++";
		case UnOpTypeSE.PREMINUSMINUS: return "--";
		case UnOpTypeSE.POSTPLUSPLUS: return "++";
		case UnOpTypeSE.POSTMINUSMINUS: return "--";
		case UnOpTypeSE.ADDRESSOF: return "&";
		case UnOpTypeSE.DEREFERENCE: return "*";
		default: throw new InternalProgramError("Unknown unary operator type: " + type);
		}

	}	
}
