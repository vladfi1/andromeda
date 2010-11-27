package com.sc2mod.andromeda.environment.scopes.content;

import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;

public class FunctionSet extends OperationSet {

	public FunctionSet(IScope owner, Operation op) {
		super(owner, op);
	}

	public FunctionSet(IScope owner, OperationSet set) {
		super(owner, set);
	}


	@Override
	protected Operation doHandleDuplicate(Operation oldOp, Operation newOp) {
		//Handle forward declarations.
		if(OperationUtil.isForwardDeclaration(oldOp)){
			oldOp.getOverrideInformation().addOverride(newOp);
			return newOp;
		} else if(OperationUtil.isForwardDeclaration(newOp)){
			newOp.getOverrideInformation().addOverride(oldOp);
			return oldOp;
		}
		//FIXME: Check that no forward declaration is undefined in the end.
		
		//Is one of them overrider?
		if(oldOp.isOverride()&&newOp.isOverride()){
			throw Problem.ofType(ProblemId.DUPLICATE_OVERRIDE_FUNCTION).at(oldOp.getDefinition(),newOp.getDefinition())
			.details(newOp.getName(),newOp.getSignature().getFullName())
			.raiseUnrecoverable();
		} else if(oldOp.isOverride()){
			return oldOp;
		} else if(newOp.isOverride()){
			return newOp;
		}
	
		//We have a conflict
		throw Problem.ofType(ProblemId.DUPLICATE_FUNCTION).at(oldOp.getDefinition(),newOp.getDefinition())
			.details(oldOp.getName(),oldOp.getSignature().getFullName())
			.raiseUnrecoverable();
	
	}

	@Override
	protected String getContentTypeName() {
		return "function";
	}

	
}
