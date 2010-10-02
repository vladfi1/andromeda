package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;

public class ClassHierachyCheckVisitor extends VoidVisitorAdapter {

	private Environment env;
	public ClassHierachyCheckVisitor(Environment env) {
		this.env = env;
	}
	
	public void execute(){
		//Resolve system classes and types
		env.typeProvider.resolveSystemTypes();
		
		//Resolve inheritance
		env.typeProvider.resolveInheritance();
		
		//Check type hierarchy
		typeProvider.checkHierarchy();
		
		//Generate class indices
		typeProvider.generateClassAndInterfaceIndex();
		
		//Resolve enrichments (We can now merge enrichments of one type)
		typeProvider.resolveEnrichments();

	}

}
