package com.sc2mod.andromeda.util.graph;

import java.util.HashSet;

public class GraphNodeSet<T> extends HashSet<GraphNode<T>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GraphNodeSet(){}
	
	public GraphNodeSet(GraphNodeSet<T> copyFrom){
		this();
		for(GraphNode<T> node : copyFrom){
			this.add(node);
		}
		
	}
	
	public GraphNode<T> getAny(){
		if(isEmpty())
			return null;
		return iterator().next();
	}
	
}
