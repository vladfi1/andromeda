package com.sc2mod.andromeda.opt.dataflow;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract super class for any type of dataflow analysis using kill and gen sets.
 * 
 * Derived concrete analysis classes must specify a kill and a gen method that construct
 * the kill and gen sets for a given CFGnode. In addition, they must specify if the analysis
 * is forward or backward and implement a factory method to create empty data sets.
 * 
 * @author gex
 *
 */
public abstract class DataflowAnalysis {
	
	public static enum Direction{
		FORWARD, BACKWARD
	}
	
	private Direction direction;
	private int analysisIndex;

	public DataflowAnalysis(Direction direction, int analysisIndex){
		this.direction = direction;
		this.analysisIndex = analysisIndex;
	}
	
	protected abstract IDataflowSet kill(CFGNode node);
	
	protected abstract IDataflowSet gen(CFGNode node);
	
	protected abstract IDataflowSet createEmptyDataSet();

	private ICFGNodeSet createNodeSet(){
		//TODO implement
		return null;
	}
	
	
	public void performAnalysis(CFGraph graph){
		initSets(graph);
		doPerform(graph);
	}

	private void initSets(CFGraph graph) {
		//Generate kill and gen sets
		for(CFGNode node : graph){
			DataflowNodeData data = node.getData(analysisIndex);
			data.killSet = kill(node);
			data.genSet = gen(node);
			data.inSet = createEmptyDataSet();
			data.outSet = createEmptyDataSet();
		}
	}

	private void doPerform(CFGraph graph) {
		int analysisIndex = this.analysisIndex;
		ICFGNodeSet workList = createNodeSet();
		boolean forward = direction == Direction.FORWARD;
		
		//Begin with the sources or sinks
		workList.addSet(forward?graph.getSources():graph.getSinks());
		
		//As long as there are nodes in the worklist continue working
		//(i.e. we haven't found the fixed point yet)
		while(!workList.isEmpty()){
			CFGNode c = workList.pop();
			DataflowNodeData data = c.getData(analysisIndex);
			IDataflowSet inSet = data.inSet;
			IDataflowSet outSet = data.outSet;
			
			//in = UNION(ALL pred.out);
			inSet.clear();
			for(CFGNode pred : (forward ? c.getPredecessors() : c.getSuccessors()) ){
				inSet.union(pred.getData(analysisIndex).inSet);
			}
			
			//out = gen UNION (in MINUS kill)
			boolean changed = outSet.setTo(inSet);
			changed |= outSet.minus(data.killSet);
			changed |= outSet.union(data.genSet);
			
			//If the outset has changed, we need to continue the analysis
			if(changed){
				workList.addSet(forward ? c.getSuccessors() : c.getPredecessors());
			}
			
			
		}
	}
}
