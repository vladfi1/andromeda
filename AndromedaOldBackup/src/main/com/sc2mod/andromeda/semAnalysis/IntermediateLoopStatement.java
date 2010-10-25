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
import com.sc2mod.andromeda.syntaxNodes.Visitor;

public class IntermediateLoopStatement extends StmtNode {
	
	private SuccessorList successors = new SuccessorList();
	
	@Override
	public SuccessorList getSuccessors() {
		return successors;
	}

	@Override
	public void accept(Visitor visitor) {
	}

	@Override
	public void childrenAccept(Visitor visitor) {
	}

	@Override
	public String toString(String tab) {
		return "End of Loop";
	}

	@Override
	public void traverseBottomUp(Visitor visitor) {
	}

	@Override
	public void traverseTopDown(Visitor visitor) {
	}

}
