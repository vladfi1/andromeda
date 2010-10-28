package com.sc2mod.andromeda.opt.dataflow;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import com.sc2mod.andromeda.util.ArrayStack;


public class CFGraph implements Iterable<CFGNode> {
	
	private ICFGNodeSet sources;
	private ICFGNodeSet sinks;
	private List<CFGNode> traversal;

	public CFGraph(ICFGNodeSet sources, ICFGNodeSet sinks, int size){
		this.sources = sources;
		this.sinks = sinks;
		createTraversal(size);
	}

	private void createTraversal(int size) {
		BitSet alreadyVisited = new BitSet(size);
		ArrayList<CFGNode> workStack = new ArrayList<CFGNode>();
		ArrayStack<CFGNode> set = new ArrayStack<CFGNode>();
		for(CFGNode c : sources){
			set.add(c);
		}
		
		while(!set.isEmpty()){
			CFGNode node = set.pop();
			//Node visited
			alreadyVisited.set(node.getIndex());
			
			for(CFGNode succ : node.getSuccessors()){
				if(!alreadyVisited.get(node.getIndex())){
					workStack.add(succ);
				}
			}
		}
	}

	public ICFGNodeSet getSources(){
		return sources;
	}

	public ICFGNodeSet getSinks(){
		return sinks;
	}

	@Override
	public Iterator<CFGNode> iterator() {
		
		return traversal.iterator();
		
	}
}
