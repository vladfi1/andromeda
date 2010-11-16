package com.sc2mod.andromeda.util.graph;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.util.ArrayStack;

public class Tarjan<T,E> {
	
	private ArrayStack<GraphNode<T>> stack = new ArrayStack<GraphNode<T>>();
	private int max_dfs;
	private GraphNodeSet<T> unvisited = new GraphNodeSet<T>();
	private Graph<T,E> graph;
	private List<GraphNodeSet<T>> result;

	
	public List<GraphNodeSet<T>> execute(Graph<T,E> graph){
		max_dfs = 0;
		stack.clear();
		this.graph = graph;
		unvisited.addAll(graph.getNodes());
		result = new ArrayList<GraphNodeSet<T>> ();
		
		while(!unvisited.isEmpty()){
			GraphNode<T> node = unvisited.getAny();
			tarjan(node);
		}
		
		return result;
			
	}

	private void tarjan(GraphNode<T> node) {
		node.dfs = max_dfs;          // Tiefensuchindex setzen
		node.lowlink = max_dfs;      // node.lowlink <= node.dfs
		max_dfs++;    // ZŠhler erhšhen
		stack.push(node);                 // node auf Stack setzen
		unvisited.remove(node);
		for(GraphEdge<T,E> e: graph.getOutgoingEdges(node)){ // benachbarte Knoten betrachten
			GraphNode<T> toNode = e.to;
			if (unvisited.contains(toNode)){
				tarjan(toNode);            // rekursiver Aufruf
				node.lowlink = Math.min(node.lowlink, toNode.lowlink);
				  // Abfragen, ob node' im Stack ist. 
				  // Bei geschickter Realisierung in O(1).
				  // (z.B. Setzen eines Bits beim Knoten beim "push" und "pop") 
			} else if (stack.contains(toNode)){
				node.lowlink = Math.min(node.lowlink, toNode.dfs);
			}
		}
		
		if (node.lowlink == node.dfs){     // Wurzel einer SZK
			GraphNodeSet<T> szk = new GraphNodeSet<T>();
			result.add(szk);
			GraphNode<T> n;
			do {
				n = stack.pop();
				szk.add(n);
			} while (n != node);
		}
	}

}
