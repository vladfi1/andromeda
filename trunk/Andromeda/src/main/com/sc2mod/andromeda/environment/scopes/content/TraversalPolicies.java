package com.sc2mod.andromeda.environment.scopes.content;

import java.util.EnumMap;

import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet.TraversalPolicy;

public class TraversalPolicies extends EnumMap<ScopedElementType, TraversalPolicy>{

	public TraversalPolicies(TraversalPolicy defaultPolicy) {
		super(ScopedElementType.class);
		for(ScopedElementType t : ScopedElementType.values()){
			put(t, defaultPolicy);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final TraversalPolicies OPERATIONS_ONLY = new TraversalPolicies(TraversalPolicy.IGNORE);
	public static final TraversalPolicies OP_SETS_ONLY = new TraversalPolicies(TraversalPolicy.IGNORE);
	
	public static final TraversalPolicies GET_ALL_BUT_RESOLVE_OP_SETS = new TraversalPolicies(TraversalPolicy.GET);
	
	
	static{
		OPERATIONS_ONLY.put(ScopedElementType.OPERATION, TraversalPolicy.GET);
		OPERATIONS_ONLY.put(ScopedElementType.PACKAGE, TraversalPolicy.RECURSE);
		OPERATIONS_ONLY.put(ScopedElementType.OP_SET, TraversalPolicy.RECURSE);
		OPERATIONS_ONLY.put(ScopedElementType.TYPE, TraversalPolicy.RECURSE);
		
		OP_SETS_ONLY.put(ScopedElementType.PACKAGE, TraversalPolicy.RECURSE);
		OP_SETS_ONLY.put(ScopedElementType.OP_SET, TraversalPolicy.GET);
		OP_SETS_ONLY.put(ScopedElementType.TYPE, TraversalPolicy.RECURSE);
		
		GET_ALL_BUT_RESOLVE_OP_SETS.put(ScopedElementType.OP_SET, TraversalPolicy.RECURSE);
	}
}
