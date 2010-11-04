package com.sc2mod.andromeda.environment.scopes.content;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;

public class NonInheritanceContentSet extends ScopeContentSet {

	public NonInheritanceContentSet(IScope scope) {
		super(scope);
	}

	@Override
	protected OperationSet createOperationSet(Operation start) {
		return new FunctionSet(this.scope,start);
	}

	@Override
	protected OperationSet createInheritedOperationSet(OperationSet copyFrom) {
		return new FunctionSet(this.scope,copyFrom);
	}

	@Override
	protected IScopedElement doHandleDuplicate(IScopedElement oldElem,
			IScopedElement newElem) {
		
		//No duplicate elements permitted
		//FIXME: Proper problem handling
		throw new Error("Duplicate element "+ oldElem.getUid());
	}

}
