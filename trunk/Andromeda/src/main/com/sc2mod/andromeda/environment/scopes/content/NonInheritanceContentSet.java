package com.sc2mod.andromeda.environment.scopes.content;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.ScopedElement;

public class NonInheritanceContentSet extends ScopeContentSet {

	public NonInheritanceContentSet(Scope scope) {
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
	protected ScopedElement doHandleDuplicate(ScopedElement oldElem,
			ScopedElement newElem) {
		
		//No duplicate elements permitted
		//FIXME: Proper problem handling
		throw new Error("Duplicate element "+ oldElem.getUid());
	}

}
