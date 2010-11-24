package com.sc2mod.andromeda.environment.scopes.content;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;

public class MethodSet extends OperationSet {

	public MethodSet(IScope owner, Operation op) {
		super(owner, op);
	}

	public MethodSet(IScope owner, OperationSet set) {
		super(owner, set);
	}
	
	public MethodSet(IScope owner, String uid) {
		super(owner,uid);
	}
	
	@Override
	protected boolean isOpInherited(Operation value) {
		return value.getContainingType()!=scope;
	}

	@Override
	protected Operation doHandleDuplicate(Operation oldOp, Operation newOp) {

		
		// Both methods defined in the same scope? Fail!
		boolean oldInherited = isOpInherited(oldOp);
		boolean newInherited = isOpInherited(newOp);
		
		//Both ops not inherited? Then we have a duplicate
		if (!oldInherited && !newInherited)
			throw Problem.ofType(ProblemId.DUPLICATE_METHOD).at(newOp.getDefinition(),oldOp.getDefinition())
						.raiseUnrecoverable();

		// Exactly one of them is static? Fail!
		if (oldOp.isStatic() ^ newOp.isStatic())
			throw Problem.ofType(ProblemId.OVERRIDE_STATIC_NON_STATIC).at(newOp.getDefinition(),oldOp.getDefinition())
						.raiseUnrecoverable();
		
		
		if (oldInherited && newInherited){
			//Both are inherited.
			//TODO: Handling if both methods are inherited (from interfaces or traits for example)
			return oldOp;
		} else {
			//One of them is inherited.
			if(oldInherited){
				return checkInheritance(oldOp,newOp);
			} else {
				return checkInheritance(newOp,oldOp);
			}
		}
		

	

	}

	/**
	 * Does checks for methods overriding inherited methods.
	 * @param superOp the inherited method
	 * @param supOp the overriding method.
	 * @return which method to add to the operation set
	 */
	private Operation checkInheritance(Operation superOp, Operation subOp) {
		
		//static methods? No inheritance checks, use the sub method
		if(subOp.isStatic()){
			return subOp;
		}
		

		// Bottom method is not declared override?
		if(!subOp.isOverride())
			throw Problem.ofType(ProblemId.OVERRIDE_WITHOUT_OVERRIDE_MODIFIER).at(subOp.getDefinition())
					.details(OperationUtil.getTypeAndNameAndSignature(subOp),
							 OperationUtil.getTypeAndNameAndSignature(superOp))
					.raiseUnrecoverable();
		
		// Different return types? Fail if not covariant
		if (!subOp.getReturnType().isSubtypeOf(superOp.getReturnType()))
			throw Problem.ofType(ProblemId.OVERRIDE_RETURN_TYPE_MISMATCH).at(subOp.getDefinition())
					.details(subOp.getReturnType().getFullName(), OperationUtil.getTypeAndNameAndSignature(subOp),
							 superOp.getReturnType().getFullName(), OperationUtil.getTypeAndNameAndSignature(superOp))
					.raiseUnrecoverable();
		
		// Top method final? Fail!
		if (superOp.isFinal())
			throw Problem.ofType(ProblemId.OVERRIDE_FINAL_METHOD).at(subOp.getDefinition())
					.raiseUnrecoverable();
		
		//Visibility reduced?
		if(subOp.getVisibility().isLessVisibleThan(superOp.getVisibility()))
			throw Problem.ofType(ProblemId.OVERRIDE_REDUCED_VISIBILITY).at(subOp.getDefinition())
					.raiseUnrecoverable();
		
		//Override permitted, add it!
		superOp.getOverrideInformation().addOverride(subOp);
		
		return subOp;
	}

	@Override
	protected String getContentTypeName() {
		return "method";
	}

}
