package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.SemanticsElement;

public class SemanticsCheckerAndResolver {

	private final ResolveAndCheckTypesVisitor resolver;
	private final SemanticsElementCheckVisitor checker;
	
	public SemanticsCheckerAndResolver(Environment env) {
		this.resolver = new ResolveAndCheckTypesVisitor(env);
		this.checker = new SemanticsElementCheckVisitor(env);
	}

	public void checkAndResolve(SemanticsElement toCheck){
		//First check and resolve types
		toCheck.accept(resolver);
		//Then, do additional checks
		toCheck.accept(checker);
	}
	
	
	
}
