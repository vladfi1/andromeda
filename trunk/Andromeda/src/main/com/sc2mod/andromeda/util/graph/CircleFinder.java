package com.sc2mod.andromeda.util.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.sc2mod.andromeda.util.ArrayStack;

public class CircleFinder<T> {
	
	private ArrayStack<GraphNode<T>> pathStack = new ArrayStack<GraphNode<T>>();
	private HashSet<GraphNode<T>> pathSet = new HashSet<GraphNode<T>>();
	
	private GraphNodeSet<T> visited = new GraphNodeSet<T>();
	private Graph<T, Boolean> graph;
	private boolean strictOnly;
	
	public List<GraphNode<T>> execute(Graph<T,Boolean> graph, boolean strictOnly){
		visited.clear();
		this.graph = graph;
		this.strictOnly = strictOnly;
		
		
		for(GraphNode<T> node : graph.getNodes()){
			if(visited.contains(node))
				continue;
			pathStack.clear();
			List<GraphNode<T>> result = process(node);
			if(result != null)
				return result;
		}
		return null;
	}

	private List<GraphNode<T>> process(GraphNode<T> node) {
		GraphEdgeSet<T, Boolean> edges = graph.getOutgoingEdges(node);
		visited.add(node);
		pathStack.push(node);
		pathSet.add(node);
		for(GraphEdge<T, Boolean> edge : edges){
			//only strict edges?
			if(!(edge.data) && strictOnly)
				continue;
			
			GraphNode<T> to = edge.to;
			
			//already visited? Possible circle or just skip
			if(visited.contains(to)){
				if(pathSet.contains(to)){
					//Circle found
					return constructResult(to);
					
					
				} else {
					//Already visited but no circle
					continue;
				}
			}
			
			//not visited, do depth first search
			List<GraphNode<T>> result = process(to);
			if(result != null){
				return result;
			}
		}
		pathStack.pop();
		pathSet.remove(node);
		
		//no circle found
		return null;
	}

	/**
	 * Constructs the result circle by reading nodes from the path stack
	 * @return
	 */
	private List<GraphNode<T>> constructResult(GraphNode<T> to) {
		ArrayList<GraphNode<T>> result = new ArrayList<GraphNode<T>>();
		int index = pathStack.indexOf(to);
		int size = pathStack.size();
		for(int i=index ; i<size ; i++){
			result.add(pathStack.get(i));
		}
		result.add(to);
		
		return result;
	}
}
