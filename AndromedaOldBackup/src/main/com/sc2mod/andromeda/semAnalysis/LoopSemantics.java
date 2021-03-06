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
import com.sc2mod.andromeda.syntaxNodes.StmtNode;

public class LoopSemantics extends SemanticsElement{

	private List<StmtNode> continues;
	private List<StmtNode> breaks;
	private static List<StmtNode> empty = new ArrayList<StmtNode>();
	
	public void addContinue(StmtNode s){
		if(continues == null){
			continues = new ArrayList<StmtNode>(2);
		}
		continues.add(s);
	}

	public void addBreak(StmtNode s){
		if(breaks == null){
			breaks = new ArrayList<StmtNode>(2);
		}
		breaks.add(s);
	}
	
	public List<StmtNode> getContinues(){
		if(continues == null) return empty;
		return continues;
	}
	
	public List<StmtNode> getBreaks(){
		if(breaks == null) return empty;
		return breaks;
	}
	
	


	
}
