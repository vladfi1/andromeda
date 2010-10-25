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

import com.sc2mod.andromeda.syntaxNodes.StmtNode;

public class SuccessorList {

	public static void addAdditionalSuccessor(StmtNode s, StmtNode toAdd){
		SuccessorList sl = s.getSuccessors();
		if(sl == null){
			s.setSuccessors(sl = new SuccessorList());
		}
		sl.add(toAdd);
	}

	void add(StmtNode s) {
		
	}
	
	
}
