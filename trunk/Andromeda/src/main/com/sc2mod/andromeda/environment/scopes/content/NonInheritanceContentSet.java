package com.sc2mod.andromeda.environment.scopes.content;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;

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
		throw Problem.ofType(ProblemId.DUPLICATE_NAME).at(newElem.getDefinition(),oldElem.getDefinition())
		.details(newElem.getUid())
		.raiseUnrecoverable();
	}

}
