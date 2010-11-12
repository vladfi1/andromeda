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

import java.util.HashSet;

import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
/**
 * Implementation of the NameProvider interface which tries
 * to create Names which are as short as possible.
 * 
 * Performance: 25000 generated Names in 6 milliseconds -> can be neglected.
 * @author J. 'gex' Finis
 *
 */
public class ShortNameProvider implements INameProvider{
	
	
	private static final int DEFAULT_NUM_NAMES_RESERVED_FOR_LOCALS = 20;
	
	
	private static final char[] ALLOWED_CHARS = 
		{'a','b','c','d','e','f','g','h','i','j','k','l','m'
		,'n','o','p','q','r','s','t','u','v','w','x','y','z'
		,'A','B','C','D','E','F','G','H','I','J','K','L','M'
		,'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
		};
	
	private static final char[] ALLOWED_CHARS_SECOND = 
	{'a','b','c','d','e','f','g','h','i','j','k','l','m'
	,'n','o','p','q','r','s','t','u','v','w','x','y','z'
	,'A','B','C','D','E','F','G','H','I','J','K','L','M'
	,'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
	,'0','1','2','3','4','5','6','7','8','9'
	};
	
	private static final int DIGIT_LENGTH = ALLOWED_CHARS_SECOND.length;
	
	private static final String[] DISALLOWED_NAMES =
	{"if","then","else","int","void","null","true","false",
	"char","text","bool","wave","unit","case","byte","do","new","get","set",
	"util","this","try"};
	
	private static final int LENGTH_1;
	private static final int LENGTH_2;
	
	static int pow(int base, int exponent){
		int result = base;
		for(;exponent>1;exponent--)result*=base;
		return result;
	}
	
	static{
		int[] LENGTH_TABLE = new int[4];
		LENGTH_TABLE[1] = ALLOWED_CHARS.length;
		for(int i=2;i<4;i++){
			LENGTH_TABLE[i] = LENGTH_TABLE[i-1] + ALLOWED_CHARS.length * pow(ALLOWED_CHARS_SECOND.length,i-1);
		}
		LENGTH_1 = LENGTH_TABLE[1];
		LENGTH_2 = LENGTH_TABLE[2];
	
	}
	
	private static HashSet<String> disallowedNames = new HashSet<String>();
	
	static {
		for(String s: DISALLOWED_NAMES){
			disallowedNames.add(s);
		}
	}

	
	
	private String[] localNames = new String[256];
	private int numLocals;
	
	private int strNum = 0;

	private char[] result = new char[5];


	private IndexInformation indexInfo;
	
	/**
	 * Generates a short name provider with an individual number of reserved local names.
	 * @param numNamesReservedForLocals number of (extra short) names reserved for locals
	 */
	public ShortNameProvider(int numNamesReservedForLocals, IndexInformation indexInfo){
		for(;numLocals<numNamesReservedForLocals;numLocals++){
			localNames[numLocals] = generate();
		}	
		this.indexInfo = indexInfo;
	}
	
	/**
	 * Standard constructor with default number of reserved names for locals.
	 */
	public ShortNameProvider(IndexInformation indexInfo) {
		this(DEFAULT_NUM_NAMES_RESERVED_FOR_LOCALS, indexInfo);
	}
	
	private String generate(){
		int strNum = this.strNum++;
		int strLen = strNum;

		char[] result = this.result;
		
		result[0] = ALLOWED_CHARS[strNum % LENGTH_1];
		strNum = (strNum-LENGTH_1)/LENGTH_1;
		if(strLen<LENGTH_1) return new String(result,0,1);
		
		result[1] = ALLOWED_CHARS_SECOND[strNum%DIGIT_LENGTH];
		strNum = (strNum-(DIGIT_LENGTH))/DIGIT_LENGTH;
		if(strLen<LENGTH_2) return new String(result,0,2);
		
		result[2] = ALLOWED_CHARS_SECOND[strNum%DIGIT_LENGTH];
		return new String(result,0,3);
		
	}
	
	private String generateCheckDisallowed(){
		String result;
		do{
			result = generate();
		} while(disallowedNames.contains(result));
		return result;
	}
	
	private String getLocalName(int index){
		while(index>=numLocals){
			localNames[numLocals] = generateCheckDisallowed();
			numLocals++;
		}
		return localNames[index];
	}
	
	public String getGlobalName(){
		return generateCheckDisallowed();
	}
	
	public void assignLocalNamesForMethod(Function function){
		int index = 0;
		Variable[] vars = function.getParams();
		for(int i=0;i<vars.length;i++){
			vars[i].setGeneratedName(getLocalName(index++));
		}
		vars = function.getLocals();
		if(vars!=null){
			for(int i=0;i<vars.length;i++){
				if(vars[i].doesOverride()) continue;
				vars[i].setGeneratedName(getLocalName(index++));
			}
		}
	}

	@Override
	public String getFunctionName(Function function) {
		if(function.isNative()){
			return function.getName();
		}
		return generateCheckDisallowed();
	}

	@Override
	public String getGlobalName(Variable decl) {
		return generateCheckDisallowed();
	}

		
	@Override
	public String getTypeName(IType type) {
		return generateCheckDisallowed();
	}

	@Override
	public void assignFieldNames(IStruct struct) {
		int index=0;
		Iterable<Variable> fields = TypeUtil.getNonStaticTypeFields(struct, false);
		for (Variable field : fields) {
			field.setGeneratedName(getLocalName(index++));
		}
	}


	@Override
	public String getFieldName(FieldDecl decl, IType clazz) {
		if(decl.isStatic()){
			return generateCheckDisallowed();
		} else {
			return getLocalName(indexInfo.getFieldIndex(decl));
		}
	}

	@Override
	public String getGlobalNameRaw(String name) {
		return generateCheckDisallowed();
	}
	
	@Override
	public String getGlobalNameRawNoPrefix(String name) {
		return generateCheckDisallowed();
	}

	@Override
	public String getLocalNameRaw(String name, int index) {
		return getLocalName(index);
	}


}
