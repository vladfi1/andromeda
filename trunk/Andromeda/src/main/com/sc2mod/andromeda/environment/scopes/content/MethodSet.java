package com.sc2mod.andromeda.environment.scopes.content;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;

public class MethodSet extends OperationSet {

	public MethodSet(Scope owner, Operation op) {
		super(owner, op);
	}

	public MethodSet(Scope owner, OperationSet set) {
		super(owner, set);
	}
	
	public MethodSet(Scope owner, String uid) {
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

		// Different return types? Fail!
		if (oldOp.getReturnType() != newOp.getReturnType())
			throw Problem.ofType(ProblemId.OVERRIDE_RETURN_TYPE_MISMATCH).at(newOp.getDefinition(),oldOp.getDefinition())
							.raiseUnrecoverable();

		// Exactly one of them is static? Fail!
		if (oldOp.isStatic() ^ newOp.isStatic())
			throw Problem.ofType(ProblemId.OVERRIDE_STATIC_NON_STATIC).at(newOp.getDefinition(),oldOp.getDefinition())
						.raiseUnrecoverable();
		
		
		if (oldInherited && newInherited){
			//Both are inherited.
			//FIXME: Handling if both methods are inherited (from interfaces or traits for example)
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
		
		//FIXME check for unimplemented methods in non abstract classes
//		//Was the old one without body? Reduce abstract count
//		if(!oldOp.hasBody()){
//			numUnimplementedMethods--;
//		}
		return subOp;
	}

	@Override
	protected String getContentTypeName() {
		return "method";
	}

}
