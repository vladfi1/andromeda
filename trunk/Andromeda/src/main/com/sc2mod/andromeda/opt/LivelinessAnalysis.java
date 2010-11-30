package com.sc2mod.andromeda.opt;

import com.sc2mod.andromeda.opt.dataflow.CFGNode;
import com.sc2mod.andromeda.opt.dataflow.DataflowAnalysis;
import com.sc2mod.andromeda.opt.dataflow.IDataflowSet;

public class LivelinessAnalysis extends DataflowAnalysis{

	public LivelinessAnalysis(int analysisIndex) {
		super(Direction.BACKWARD, analysisIndex);
	}

	@Override
	protected IDataflowSet createEmptyDataSet() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	protected IDataflowSet gen(CFGNode node) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	protected IDataflowSet kill(CFGNode node) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

}
