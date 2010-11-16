package com.sc2mod.andromeda.util.graph;

import java.util.List;

public class CircleElimination<T> {

	private Tarjan<T, Boolean> tarjan = new Tarjan<T, Boolean>();
	
	/**
	 * Finds in a strongly connected component (scc) the next edge that is best to be removed to break the scc into single
	 * nodes where no node is strongly connected to another by removing the least number of edges possible.
	 * 
	 * This is only a heuristics and does not necessarily compute the perfect solution.
	 * 
	 * If all edges in the scc are strict, then no edge is returned (because strict edges cannot be removed)
	 * 
	 * @param graph the graph to which the scc belongs (used to find the edges)
	 * @param scc the scc
	 * @return the edge to be removed
	 */
	private GraphEdge<T, Boolean> findEdgeToEliminate(Graph<T,Boolean> graph, GraphNodeSet<T> scc){
		GraphEdge<T, Boolean> result = null;
		int minEdges = Integer.MAX_VALUE;
		for(GraphNode<T> node : scc){
			GraphEdgeSet<T, Boolean> edges = graph.getOutgoingEdges(node);
			
			//Check if there are edges that can be removed and how many edges go to the szk
			int numEdges = 0;
			GraphEdge<T, Boolean> candidate = null;
			for(GraphEdge<T,Boolean> edge : edges){
				
				//Target not in szk? Skip!
				if(!scc.contains(edge.to)){
					continue;
				}
				
				//count
				numEdges++;
				
				//Not strikt? Then this is a candidate for removal
				if(!edge.data){
					candidate = edge;
				}
			}
			
			//Edge with the least outgoing edges becomes result
			if(candidate != null && numEdges < minEdges){
				minEdges = numEdges;
				result = candidate;
			}
		}
		
		return result;
	}
	
	public GraphNodeSet<T> execute(Graph<T,Boolean> graph){
		boolean removed;
		do{
			List<GraphNodeSet<T>> sccs = tarjan.execute(graph);
			removed = false;
			for(GraphNodeSet<T> scc : sccs){
				
				//If the scc is trivial, no circle
				if(scc.size() == 1)
					continue;
				
				//Circle found, try to break it by removing an edge.
				GraphEdge<T, Boolean> edge = findEdgeToEliminate(graph, scc);
				
				//No edge can be removed? return this node set.
				if(edge == null){
					return scc;
				}
				
				//Remove that edge from the graph
				graph.removeEdge(edge);
				removed = true;
				System.out.println("Removing " + edge);
			}
		
		//Loop until there are no edges removed anymore (i.e. no more circles).
		} while(removed);
		
		return null;
	}
}
