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
import com.sc2mod.andromeda.syntaxNodes.util.NoParamVisitor;
import com.sc2mod.andromeda.syntaxNodes.util.NoResultVisitor;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitor;

public class IntermediateLoopStatement extends StmtNode {
	
	private SuccessorList successors = new SuccessorList();
	
	@Override
	public SuccessorList getSuccessors() {
		return successors;
	}


//	@Override
//	public String toString(String tab) {
//		return "End of Loop";
//	}

	@Override
	public void accept(VoidVisitor visitor) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P, R> R accept(
			com.sc2mod.andromeda.syntaxNodes.util.Visitor<P, R> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P> void accept(NoResultVisitor<P> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public void childrenAccept(VoidVisitor visitor) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P, R> R childrenAccept(
			com.sc2mod.andromeda.syntaxNodes.util.Visitor<P, R> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P> void childrenAccept(NoResultVisitor<P> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}


	@Override
	public <R> R accept(NoParamVisitor<R> visitor) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}


	@Override
	public <R> R childrenAccept(NoParamVisitor<R> visitor) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

}
