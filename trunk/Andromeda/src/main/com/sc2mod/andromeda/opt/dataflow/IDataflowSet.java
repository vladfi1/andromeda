package com.sc2mod.andromeda.opt.dataflow;

public interface IDataflowSet {

	public boolean union(IDataflowSet otherSet);
	
	public boolean minus(IDataflowSet otherSet);
	
	public boolean setTo(IDataflowSet initWith);
	
	public void clear();
}
