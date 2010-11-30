package com.sc2mod.andromeda.environment.scopes;

public enum UsageType {
	LVALUE, RVALUE, LRVALUE, OTHER;

	//TODO Add SCOPE_PREFIX here so the checking for correct prefix type (type, package, var only as non prefix) can be done in ResolveUtil
}
