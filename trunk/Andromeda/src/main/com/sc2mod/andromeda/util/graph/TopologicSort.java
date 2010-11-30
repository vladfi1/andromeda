package com.sc2mod.andromeda.util.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sc2mod.andromeda.problems.InternalProgramError;

public class TopologicSort<T,E> {

	
	public List<GraphNode<T>> execute(Graph<T,E> graph){
		
		LinkedList<GraphNode<T>> noPredecessors = new LinkedList<GraphNode<T>>();
		ArrayList<GraphNode<T>> result = new ArrayList<GraphNode<T>>(graph.getNodes().size());
		
		//Set predecessors to 0
		for(GraphNode<T> node : graph.getNodes()){
			node.lowlink = 0;
		}
		
		//calculate number of predecessors
		for(GraphNode<T> node : graph.getNodes()){
			for(GraphEdge<T, E> edge : graph.getOutgoingEdges(node)){
				edge.to.lowlink++;
			}
		}
		
		//Get nodes without predecessor
		for(GraphNode<T> node : graph.getNodes()){
			if(node.lowlink == 0){
				noPredecessors.addFirst(node);
			}
		}
		
		if(noPredecessors.isEmpty()){
			throw new InternalProgramError("Graph is strongly connected, cannot do topologic sorting!");
		}
		
		//Topologic node removing
		int num = noPredecessors.size();
		while(!noPredecessors.isEmpty()){
			GraphNode<T> node = noPredecessors.removeLast();
			
			//Add to result
			result.add(node);
			
			//Decrement predecessor count of all successors, add to list if it gets 0
			GraphEdgeSet<T, E> edges = graph.getOutgoingEdges(node);
			for(GraphEdge<T, E> edge : edges){
				int preds = --edge.to.lowlink;
				if(preds == 0){
					noPredecessors.addFirst(edge.to);
					num++;
				}
			}

		}
		
		//No more nodes without predecessor but graph not empty? Then we have a cycle
		if(graph.getNodes().size() != num)
			throw new InternalProgramError("Graph contained circle, couldn't do a topologic sort");
		
		return result;
	}
}
