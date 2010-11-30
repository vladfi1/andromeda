package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;

public class ScopeNameVisitor extends ParameterSemanticsVisitorAdapter<Void, String> {

	@Override
	public String visit(Package package1, Void state) {
		return "package";
	}
	
	
	
	
}
