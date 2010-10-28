package com.sc2mod.andromeda.opt.dataflow;

public class CFGNodeSetRecycleFactory extends RecyclingFactory<ICFGNodeSet> {

	protected CFGNodeSetRecycleFactory() {
		super(256);
	}

	public ICFGNodeSet create(CFGNode node) {
		ICFGNodeSet set = super.create();
		set.add(node);
		return set;
	}

	public ICFGNodeSet create() {
		return super.create();
	}
	
	@Override
	public void release(ICFGNodeSet node) {
		node.clear();
		super.release(node);
	}

	@Override
	protected ICFGNodeSet createNew() {
		return new ArrayCFGNodeSet();
	}

}
