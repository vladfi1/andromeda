package com.sc2mod.andromeda.util.graph;

import java.util.HashMap;

public class Graph<T,E> {
	private final GraphEdgeSet<T,E> EMPTY_NODE_SET = new GraphEdgeSet<T,E>();
	
	private final GraphNodeSet<T> nodes = new GraphNodeSet<T>();
	private final GraphEdgeSet<T,E> edges = new GraphEdgeSet<T,E>();
	private final HashMap<GraphNode<T>, GraphEdgeSet<T,E>> outgoingEdges = new HashMap<GraphNode<T>, GraphEdgeSet<T,E>>();
	
	public GraphNodeSet<T> getNodes(){
		return nodes;
	}
	
	public boolean addNode(GraphNode<T> node){
		return nodes.add(node);
	}
	
	public void addEdge(GraphNode<T> from, GraphNode<T> to, E data){
		GraphEdgeSet<T,E> outgoing = null;
		if(!nodes.contains(from)){
			nodes.add(from);
		} else {
			outgoing = outgoingEdges.get(from);
		}
		if(!nodes.contains(to)){
			nodes.add(to);
		}
		GraphEdge<T,E> ge = new GraphEdge<T,E>(from, to, data);
		
		if(outgoing == null){
			outgoing = new GraphEdgeSet<T,E>();
			outgoingEdges.put(from, outgoing);
		}
		outgoing.add(ge);
		if(!edges.add(ge)){
			throw new RuntimeException("Duplicate edge" + ge);
		}
	}
	
	public boolean removeEdge(GraphEdge<T, E> edge){
		if(!edges.contains(edge))
			return false;
		
		//remove from edge set
		edges.remove(edge);
		
		//remove from outgoing set of source node
		GraphEdgeSet<T, E> outgoing = outgoingEdges.get(edge.from);
		outgoing.remove(edge);
		return true;
	}
	
	
	public GraphEdgeSet<T,E> getOutgoingEdges(GraphNode<T> node){
		GraphEdgeSet<T,E> o = outgoingEdges.get(node);
		if(o == null){
			return EMPTY_NODE_SET;
		}
		return o;
	}
}
