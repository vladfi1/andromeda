package com.sc2mod.andromeda.opt.dataflow;

import java.util.Iterator;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Represents a node in a control flow graph (CFG).
 * 
 * It has links to its predecessors, successors and to the underlying syntax node.
 * It also has an index which can be used for fast adding to or from sets.
 * 
 * For convenience and speed, it also has containers the kill/gen/in/out sets of all analysis
 * to be done on this node. It is built to be reused.
 * 
 * @author gex
 *
 */
public class CFGNode implements ICFGNodeSet{
	
	private DataflowNodeData[] analysisData;
	public CFGNode(int numAnalysis){
		analysisData = new DataflowNodeData[numAnalysis];
		for(int i = 0;i<numAnalysis;i++){
			analysisData[i] = new DataflowNodeData();
		}
	}
	
	private ICFGNodeSet predecessors;


	private ICFGNodeSet successors;
	private SyntaxNode content;
	public int index;
	
	public void set(int index, SyntaxNode content){
		this.index = index;
		this.content = content;
	}
	
	public int getIndex(){
		return index;
	}
	
	public ICFGNodeSet getPredecessors() {
		return predecessors;
	}

	public ICFGNodeSet getSuccessors() {
		return successors;
	}

	public void addPredecessorSet(ICFGNodeSet set){
		predecessors.addSet(set);
		for(CFGNode preds : predecessors){
			preds.successors.add(this);
		}
	}
	
	
	public void addSuccessor(CFGNode cond) {
		successors.add(cond);
		cond.predecessors.add(this);
	}
	
	public SyntaxNode getContent(){
		return content;
	}
	
	public DataflowNodeData getData(int index){
		return analysisData[index];
	}

	//Nodeset methods
	@Override public void add(CFGNode add) { throw new Error("Cannot add to a node!"); }
	@Override public void addSet(ICFGNodeSet set) { throw new Error("Cannot add to a node!"); }
	@Override public boolean isEmpty() { return false; }
	@Override public CFGNode peek() { return this; }
	@Override public CFGNode pop() { throw new Error("Cannot pop!"); }
	@Override public void clear() { throw new Error("Cannot clear a cfg node!"); }

	@Override
	public Iterator<CFGNode> iterator() {
		return new Iterator<CFGNode>() {
			boolean iterated;
			@Override
			public void remove() {
				throw new Error("Cannot remove!");
			}
			
			@Override
			public CFGNode next() {
				if(iterated) return null;
				iterated = true;
				return CFGNode.this;
			}
			
			@Override
			public boolean hasNext() {
				return !iterated;
			}
		};
	}



}
