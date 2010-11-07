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
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;

public class LoopSemantics implements SemanticsElement{

	private List<StmtNode> continues;
	private List<StmtNode> breaks;
	private boolean controlFlowReachesEnd = true;
	private static List<StmtNode> empty = new ArrayList<StmtNode>();
	
	public LoopSemantics() {
	}
	
	public LoopSemantics(LoopSemantics semantics) {
		this.continues = semantics.continues;
		this.breaks = semantics.breaks;
		this.controlFlowReachesEnd = semantics.controlFlowReachesEnd;
	}

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

	@Override
	public void accept(VoidSemanticsVisitor visitor) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P> void accept(NoResultSemanticsVisitor<P> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P, R> R accept(ParameterSemanticsVisitor<P, R> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	public void setControlFlowReachesEnd(boolean b) {
		controlFlowReachesEnd = b;
	}
	
	public boolean doesControlFlowReachEnd() {
		return controlFlowReachesEnd;
	}
	


	
}
