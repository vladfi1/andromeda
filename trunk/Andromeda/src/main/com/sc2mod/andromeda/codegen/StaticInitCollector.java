package com.sc2mod.andromeda.codegen;


import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.visitors.VoidTreeScanVisitor;

/**
 * Does the static init sorting and returns the properly sorted
 * list of all static inits in the program.
 * The static inits should be called in the order returned by this class.
 * @author gex
 *
 */
public class StaticInitCollector {


	private ArrayList<StaticInit> result;
	
	public List<StaticInit> getSortedInits(SyntaxNode input){
		result = new ArrayList<StaticInit>();
		
		input.accept(new StaticInitVisitor());
		
		//No sorting has to be done anymore, this was done earlier (directly after the parsing) by sorting
		//the compilation units topologically
		
		return result;
	}
	
	private class StaticInitVisitor extends VoidTreeScanVisitor{
		
		@Override
		public void visit(StaticInitDeclNode staticInitDeclNode) {
			result.add(staticInitDeclNode.getSemantics());
		}
	}
}
