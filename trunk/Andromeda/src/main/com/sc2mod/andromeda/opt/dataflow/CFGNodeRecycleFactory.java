package com.sc2mod.andromeda.opt.dataflow;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Factory for cfg nodes which allows to recycle them for less object creation overhead.
 * @author gex
 *
 */
public class CFGNodeRecycleFactory extends RecyclingFactory<CFGNode> {

	private int numAnalysis;

	public CFGNodeRecycleFactory(int numAnalysis){
		super(256);
		this.numAnalysis = numAnalysis;
	}
	
	@Override
	protected CFGNode createNew() {
		return new CFGNode(numAnalysis);
	}
	
	public CFGNode create(int index, SyntaxNode content){
		CFGNode node = create();
		node.set(index, content);
		return node;
	}
	
	public void release(CFGNode node){
		//TODO: Recycle the pred and succ sets here
		super.release(node);
	}


}
