/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.syntaxNodes.Statement;

public class LoopSemantics extends SemanticsElement{

	private List<Statement> continues;
	private List<Statement> breaks;
	private static List<Statement> empty = new ArrayList<Statement>();
	
	public void addContinue(Statement s){
		if(continues == null){
			continues = new ArrayList<Statement>(2);
		}
		continues.add(s);
	}

	public void addBreak(Statement s){
		if(breaks == null){
			breaks = new ArrayList<Statement>(2);
		}
		breaks.add(s);
	}
	
	public List<Statement> getContinues(){
		if(continues == null) return empty;
		return continues;
	}
	
	public List<Statement> getBreaks(){
		if(breaks == null) return empty;
		return breaks;
	}
	
	


	
}
