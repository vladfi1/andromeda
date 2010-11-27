package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ImportListNode;
import com.sc2mod.andromeda.syntaxNodes.ImportNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.util.graph.CircleElimination;
import com.sc2mod.andromeda.util.graph.CircleFinder;
import com.sc2mod.andromeda.util.graph.Graph;
import com.sc2mod.andromeda.util.graph.GraphNode;
import com.sc2mod.andromeda.util.graph.TopologicSort;

public class InputSorter {
	
	private HashMap<String, GraphNode<SourceFileNode>> cuNameMap;
	private List<SourceFileNode> unnamedCUs;
	

	public List<SourceFileNode> sort(List<SourceFileNode> srcs) {
		cuNameMap = new HashMap<String, GraphNode<SourceFileNode>>();
		unnamedCUs = new ArrayList<SourceFileNode>();
		
		//Remove natives, from list, they are not sorted
		List<SourceFileNode> natives = new ArrayList<SourceFileNode>();
		for(Iterator<SourceFileNode> it = srcs.iterator();it.hasNext();){
			SourceFileNode src = it.next();
			if(src.getSourceInfo().getType() == InclusionType.NATIVE){
				it.remove();
				natives.add(src);
			}
		}
		
		
		buildCompilationUnitMap(srcs);
		
		Graph<SourceFileNode,Boolean> graph = buildGraph(srcs);
		
		//check for circles of strict imports
		List<GraphNode<SourceFileNode>> circle = new CircleFinder<SourceFileNode>().execute(graph, true);
		if(circle != null){
			//Strict circle found, error!
			StringBuilder circleBuilder = new StringBuilder(128);
			for(GraphNode<SourceFileNode> node : circle){
				circleBuilder.append(node.content.getSourceInfo().getQualifiedName()).append("\n");
			}
			
			throw Problem.ofType(ProblemId.CYCLE_IN_STRICT_IMPORTS).details(circleBuilder.substring(0, circleBuilder.length()-1))
				.raiseUnrecoverable();
		}
		
		//Eliminate non-strict circles
		CircleElimination<SourceFileNode> eliminator = new CircleElimination<SourceFileNode>();
		eliminator.execute(graph);
		
		//Now we have an acyclic graph. Do topologic sorting
		List<GraphNode<SourceFileNode>> sortedResult = new TopologicSort<SourceFileNode, Boolean>().execute(graph);	
		
		//Unwrap the list to get the result
		ArrayList<SourceFileNode> result = new ArrayList<SourceFileNode>(natives.size() + sortedResult.size());
		//Readd natives
		for(SourceFileNode s : natives){
			result.add(s);
		}
		for(GraphNode<SourceFileNode> node : sortedResult){
			result.add(node.content);
		}
		
		
		
		return result;
	}

	private void buildCompilationUnitMap(List<SourceFileNode> srcs) {
		for(SourceFileNode s : srcs){
			
			String cuName = s.getSourceInfo().getQualifiedName();
			if(cuName == null){
				unnamedCUs.add(s);
			} else {
				GraphNode<SourceFileNode> old = cuNameMap.put(cuName, new GraphNode<SourceFileNode>(s));
				if(old != null)
					throw new InternalProgramError("Duplicate cu named " + cuName);
			}
		}
	}

	private Graph<SourceFileNode, Boolean> buildGraph(List<SourceFileNode> srcs) {
		Graph<SourceFileNode, Boolean> graph = new Graph<SourceFileNode, Boolean>();
		for(SourceFileNode src : srcs){
			//Add this source as node
			String cuName = src.getSourceInfo().getQualifiedName();
			GraphNode<SourceFileNode> node;
			if(cuName == null){
				node = new GraphNode<SourceFileNode>(src);
			} else {
				node = cuNameMap.get(cuName);
			}
			graph.addNode(node);
			
			
	
			ImportListNode imports = src.getImports();
			if(imports != null){
				for(ImportNode imp : imports){
					
					String importName = ImportResolver.getImportString(imp);
					GraphNode<SourceFileNode> imported = cuNameMap.get(importName);
					
					//Add edge for import
					graph.addEdge(node, imported, imp.isStrict());
				}
			}
			
		}
		return graph;
	}
}
