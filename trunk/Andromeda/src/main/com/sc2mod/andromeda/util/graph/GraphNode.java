package com.sc2mod.andromeda.util.graph;

public class GraphNode<T> {
	
	private int hashCode = (int) (Math.random()*Integer.MAX_VALUE);
	
	@Override
	public int hashCode() {
		return hashCode;
	}

	public final T content;
	
	public int dfs;
	public int lowlink;
	
	public GraphNode(T content){
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "(" + content + ")";
	}
}
