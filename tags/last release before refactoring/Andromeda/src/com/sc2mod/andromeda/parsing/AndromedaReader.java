/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilterReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parser.UnicodeEscapes;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class AndromedaReader extends FilterReader{

	private int index;
	private int inclusionType;
	private SourceEnvironment environment;
	public int getInclusionType() {
		return inclusionType;
	}	
		
	AndromedaReader(SourceEnvironment s, Source f, int inclusionType, int count) throws IOException{
		super(f.createReader());
		this.inclusionType = inclusionType;
		this.index = count;
		this.environment = s;
	}
		
	public int getFileId(){
		return index<<24;		
	}

	public SourceEnvironment getSourceEnvironment() {
		return environment;
	}
	
//	public String getSourceName(){
//		//TODO stub!
//		return "bla";
//	}
	

	

	
}
