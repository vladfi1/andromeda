/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.syntaxNodes;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.syntaxNodes.util.VisitorNode;
import com.sc2mod.andromeda.problems.InternalProgramError;

public abstract class SyntaxNode implements VisitorNode {
	private int left;
	private int right;
	
	public abstract SyntaxNode getParent();

	public abstract void setParent(SyntaxNode parent);

	public SyntaxNode setPos(int left, int right) {
		this.left = left;
		this.right = right;
		return this;
	}
	
	public SyntaxNode setPos(SyntaxNode leftNode, int leftAlternative, int right) {
		int leftN = leftNode.left;
		if(leftN!=-1){
			this.left = leftN;
		} else {
			this.left = leftAlternative;
		}
		this.right = right;
		return this;
	}

	public int getLeftPos() {
		return left;
	}

	public int getRightPos() {
		return right;
	}
	
	public void setSemantics(SemanticsElement s){
		throw new InternalProgramError("Cannot set semantics for " + this.getClass().getSimpleName() + ". Not defined for this class!");
	}
	
	public SemanticsElement getSemantics(){
		throw new InternalProgramError("Cannot get semantics for " + this.getClass().getSimpleName() + ". Not defined for this class!");
	}

}
