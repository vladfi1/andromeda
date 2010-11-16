package com.sc2mod.andromeda.util.graph;

import java.util.HashSet;

public class GraphEdgeSet<T,E> extends HashSet<GraphEdge<T,E>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GraphEdgeSet(){}
	
	public GraphEdgeSet(GraphEdgeSet<T,E> copyFrom){
		this();
		for(GraphEdge<T,E> node : copyFrom){
			this.add(node);
		}
		
	}
	
	public GraphEdge<T,E> getAny(){
		if(isEmpty())
			return null;
		return iterator().next();
	}
	
}
