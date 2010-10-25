package com.sc2mod.andromeda.codegen;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.ScopeUtil;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;
import com.sc2mod.andromeda.util.visitors.VoidTreeScanVisitor;

/**
 * Does the static init sorting and returns the properly sorted
 * list of all static inits in the program.
 * The static inits should be called in the order returned by this class.
 * @author gex
 *
 */
//TODO: Implement proper sorting algorithm (further discuss on sc2mod.com)
public class StaticInitSorter {


	private ArrayList<StaticInit> result;
	
	public List<StaticInit> getSortedInits(SyntaxNode input){
		result = new ArrayList<StaticInit>();
		
		input.accept(new StaticInitVisitor());
		
		//XPilot: sort global inits by scope (high inclusion type => call it first)
		Collections.sort(result, new Comparator<StaticInit>() {
			@Override
			public int compare(StaticInit arg0, StaticInit arg1) {
				return ScopeUtil.getFileScopeOfScope(arg1.getScope()).getInclusionType().getPriority() - ScopeUtil.getFileScopeOfScope(arg0.getScope()).getInclusionType().getPriority();
			}
		});
		
		return result;
	}
	
	private class StaticInitVisitor extends VoidTreeScanVisitor{
		
		@Override
		public void visit(StaticInitDeclNode staticInitDeclNode) {
			result.add(staticInitDeclNode.getSemantics());
		}
	}
}
