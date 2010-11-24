/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen.buffers;

import com.sc2mod.andromeda.codegen.CodeGenerator;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;

/**
 * 
 *	Common buffer used for most actions.
 * 
 * @author J. 'gex' Finis
 */
public class AdvancedBuffer extends SimpleBuffer {

	private boolean whitespaceInExprs;
	private boolean newLineBeforeBraces;
	public AdvancedBuffer(int initialSize, Configuration conf) {
		super(initialSize, conf);
		whitespaceInExprs = conf.getParamBool(Parameter.CODEGEN_WHITESPACES_IN_EXPRS);
		newLineBeforeBraces = conf.getParamBool(Parameter.CODEGEN_OWN_LINE_FOR_OPEN_BRACES);
	}
	
	public AdvancedBuffer exprWhitespace(){
		if(whitespaceInExprs){
			append(" ");
		}
		return this;
	}
	
	public AdvancedBuffer nlBeforeBrace(){
		if(newLineBeforeBraces){
			nl();
		}
		return this;
	}
	
	
}
