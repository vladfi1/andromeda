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
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.syntaxNodes.AssignmentOperatorType;
import com.sc2mod.andromeda.syntaxNodes.BinaryOperator;
import com.sc2mod.andromeda.syntaxNodes.UnaryOperator;

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
	}
	
	public static StringPair getCastOp(Type fromType, Type toType){
		HashMap<Type, StringPair> tcs = typeCasts.get(fromType);
		if(tcs==null) return null;
		return tcs.get(toType);
	}
	
	
	
	public static String getAssignOp(int type, boolean whitespaces){
		if(whitespaces){
			switch(type){
			case AssignmentOperatorType.EQ: return " = ";
			case AssignmentOperatorType.MULTEQ: return " *= ";
			case AssignmentOperatorType.DIVEQ: return " /= ";
			case AssignmentOperatorType.MODEQ: return " %= ";
			case AssignmentOperatorType.PLUSEQ: return " += ";
			case AssignmentOperatorType.MINUSEQ: return " -= ";
			case AssignmentOperatorType.LSHIFTEQ: return " <<= ";
			case AssignmentOperatorType.RSHIFTEQ: return " >>= ";
			case AssignmentOperatorType.URSHIFTEQ: return " >>>= ";
			case AssignmentOperatorType.ANDEQ: return " &= ";
			case AssignmentOperatorType.XOREQ: return " ^= ";
			case AssignmentOperatorType.OREQ: return " |= ";		
			default: throw new CompilationError("Unkonwn assignment operator type: " + type);
			}	
		}
		switch(type){
		case AssignmentOperatorType.EQ: return "=";
		case AssignmentOperatorType.MULTEQ: return "*=";
		case AssignmentOperatorType.DIVEQ: return "/=";
		case AssignmentOperatorType.MODEQ: return "%=";
		case AssignmentOperatorType.PLUSEQ: return "+=";
		case AssignmentOperatorType.MINUSEQ: return "-=";
		case AssignmentOperatorType.LSHIFTEQ: return "<<=";
		case AssignmentOperatorType.RSHIFTEQ: return ">>=";
		case AssignmentOperatorType.URSHIFTEQ: return ">>>=";
		case AssignmentOperatorType.ANDEQ: return "&=";
		case AssignmentOperatorType.XOREQ: return "^=";
		case AssignmentOperatorType.OREQ: return "|=";
		default: throw new CompilationError("Unkonwn assignment operator type: " + type);
		}	
	}
	

	
	public static String getBinaryOp(int type, boolean whitespaces){
		if(whitespaces){
			switch(type){
			case BinaryOperator.OROR: return " || ";
			case BinaryOperator.ANDAND: return " && ";
			case BinaryOperator.OR: return " | ";
			case BinaryOperator.AND: return " & ";
			case BinaryOperator.XOR: return " ^ ";
			case BinaryOperator.EQEQ: return " == ";
			case BinaryOperator.NOTEQ: return " != ";
			case BinaryOperator.GT: return " > ";
			case BinaryOperator.LT: return " < ";
			case BinaryOperator.GTEQ: return " >= ";
			case BinaryOperator.LTEQ: return " <= ";
			case BinaryOperator.LSHIFT: return " << ";
			case BinaryOperator.RSHIFT: return " >> ";
			case BinaryOperator.URSHIFT: return " >>> ";
			case BinaryOperator.PLUS: return " + ";
			case BinaryOperator.MINUS: return " - ";
			case BinaryOperator.MULT: return " * ";
			case BinaryOperator.DIV: return " / ";
			case BinaryOperator.MOD: return " % ";	
			default: throw new CompilationError("Unkonwn binary operator type: " + type);
			}
		}
	
		switch(type){
		case BinaryOperator.OROR: return "||";
		case BinaryOperator.ANDAND: return "&&";
		case BinaryOperator.OR: return "|";
		case BinaryOperator.AND: return "&";
		case BinaryOperator.XOR: return "^";
		case BinaryOperator.EQEQ: return "==";
		case BinaryOperator.NOTEQ: return "!=";
		case BinaryOperator.GT: return ">";
		case BinaryOperator.LT: return "<";
		case BinaryOperator.GTEQ: return ">=";
		case BinaryOperator.LTEQ: return "<=";
		case BinaryOperator.LSHIFT: return "<<";
		case BinaryOperator.RSHIFT: return ">>";
		case BinaryOperator.URSHIFT: return ">>>";
		case BinaryOperator.PLUS: return "+";
		case BinaryOperator.MINUS: return "-";
		case BinaryOperator.MULT: return "*";
		case BinaryOperator.DIV: return "/";
		case BinaryOperator.MOD: return "%";	
		default: throw new CompilationError("Unkonwn binary operator type: " + type);
		}
	}
	

	
	public static String getUnaryOp(int type){
		switch(type){
		case UnaryOperator.COMP: return "~";
		case UnaryOperator.MINUS: return "-";
		case UnaryOperator.NOT: return "!";
		case UnaryOperator.PREPLUSPLUS: return "++";
		case UnaryOperator.PREMINUSMINUS: return "--";
		case UnaryOperator.POSTPLUSPLUS: return "++";
		case UnaryOperator.POSTMINUSMINUS: return "--";
		case UnaryOperator.ADDRESSOF: return "&";
		case UnaryOperator.DEREFERENCE: return "*";
		default: throw new CompilationError("Unkonwn unary operator type: " + type);
		}

	}
	
	


	
}
