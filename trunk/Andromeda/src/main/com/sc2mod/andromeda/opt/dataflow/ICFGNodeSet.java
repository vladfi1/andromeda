package com.sc2mod.andromeda.opt.dataflow;

public interface ICFGNodeSet extends Iterable<CFGNode> {

	public CFGNode peek();
	
	public CFGNode pop();
	
	public void addSet(ICFGNodeSet set);
	
	public boolean isEmpty();
	
	public void add(CFGNode add);
	
	public void clear();

//	public void remove(CFGNode remove);
}
