package com.sc2mod.andromeda.test.junit.unittests.graph;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.sc2mod.andromeda.util.graph.CircleElimination;
import com.sc2mod.andromeda.util.graph.CircleFinder;
import com.sc2mod.andromeda.util.graph.Graph;
import com.sc2mod.andromeda.util.graph.GraphNode;
import com.sc2mod.andromeda.util.graph.GraphNodeSet;
import com.sc2mod.andromeda.util.graph.Tarjan;

public class GraphTest{

	
	@Test
	public void tarjanTest(){
		Graph<Integer,Boolean> g = new Graph<Integer,Boolean>();
		
		GraphNode<Integer> 
			g1 = new GraphNode<Integer>(1),
			g2 = new GraphNode<Integer>(2),
			g3 = new GraphNode<Integer>(3),
			g4 = new GraphNode<Integer>(4),
			g5 = new GraphNode<Integer>(5),
			g6 = new GraphNode<Integer>(6),
			g7 = new GraphNode<Integer>(7),
			g8 = new GraphNode<Integer>(8),
			g9 = new GraphNode<Integer>(9),
			g10 = new GraphNode<Integer>(10),
			g11 = new GraphNode<Integer>(11),
			g12 = new GraphNode<Integer>(12);
			
		g.addEdge(g1, g2,false);
		g.addNode(g3);
		g.addEdge(g2, g1,false);
		
		g.addEdge(g4, g5,false);
		g.addEdge(g5, g6,false);
		g.addEdge(g6, g4,false);
		
		g.addEdge(g7, g8,false);
		g.addEdge(g8, g9,false);
		g.addEdge(g9, g10,false);
		g.addEdge(g10, g11,false);
		g.addEdge(g9, g7,false);
		g.addEdge(g10, g12,false);
		g.addEdge(g11, g8,false);
		g.addEdge(g11, g10,false);
		

		List<GraphNode<Integer>> cfResult = new CircleFinder<Integer>().execute(g, false);
		System.out.println("CIRCLE: " + cfResult);
		if(cfResult == null){
			fail("No circle found");
		}
		
		GraphNodeSet<Integer> result = new CircleElimination<Integer>().execute(g);
		if(result != null){
			fail("Could not delete all circles");
		}
		
		List<GraphNodeSet<Integer>> tarjanResult = new Tarjan<Integer, Boolean>().execute(g);
		
		if(tarjanResult.size() != g.getNodes().size()){
			fail("There are still sccs");
		}
		

		g = new Graph<Integer,Boolean>();
		
		g.addEdge(g1, g2, true);
		g.addEdge(g2, g3, true);
		g.addEdge(g3, g1, true);
		
		result = new CircleElimination<Integer>().execute(g);
		if(result == null){
			fail("Eliminated strict circle");
		}
		
		System.out.println(result);
		
		cfResult = new CircleFinder<Integer>().execute(g, true);
		System.out.println("CIRCLE: " + cfResult);
		if(cfResult == null){
			fail("No circle found");
		}
		
	}
}
