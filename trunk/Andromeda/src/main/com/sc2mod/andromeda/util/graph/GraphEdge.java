package com.sc2mod.andromeda.util.graph;

public class GraphEdge<T,E> {

	public final GraphNode<T> from;
	public final GraphNode<T> to;
	public final E data;
	
	public GraphEdge(GraphNode<T> from, GraphNode<T> to, E data){
		this.to = to;
		this.from = from;
		this.data = data;
	}
	
	@Override
	public int hashCode() {
		return to.hashCode() + from.hashCode() * 31;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj instanceof GraphEdge<?,?>){
			GraphEdge<?,?> g = (GraphEdge<?,?>) obj;
			return g.to.equals(to) && g.from.equals(from);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return from + " --> " + to; 
	}
}
