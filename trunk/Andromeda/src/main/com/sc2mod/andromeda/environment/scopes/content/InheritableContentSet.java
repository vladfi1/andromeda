package com.sc2mod.andromeda.environment.scopes.content;

import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.ScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;

public class InheritableContentSet extends ScopeContentSet {

	public InheritableContentSet(Scope scope) {
		super(scope);
	}

	@Override
	protected OperationSet createOperationSet(Operation start) {
		return new MethodSet(this.scope,start);
	}

	@Override
	protected OperationSet createInheritedOperationSet(OperationSet copyFrom) {
		return new MethodSet(this.scope,copyFrom);
	}
	
	@Override
	public boolean supportsInheritance() {
		return true;
	}
	
	@Override
	public boolean isElementInherited(ScopedElement value) {
		return value.getContainingType()!=scope;
	}
	
	public void addInheritedContent(ScopeContentSet parentSet) {
		for(Entry<String, ScopedElement> e : parentSet.contentSet.entrySet()){
			ScopedElement elem = e.getValue();
			
			//Operation set handling
			if(elem.getElementType() == ScopedElementType.OP_SET){
				addInheritedOpSet(e.getKey(), (OperationSet) elem);
				continue;
			}
			
			//Do not inherit private values
			if(!elem.getVisibility().isInherited()) continue;
			
			//Add the element
			add(e.getKey(),elem);
				
		}
	
	}
	
	private void addInheritedOpSet(String name, OperationSet set){
		ScopedElement o = contentSet.get(name);
		if(o != null){
			//Op set present, add the method to it
			if(o.getElementType() != ScopedElementType.OP_SET){
				throw new Error("Trying to override non op set with operation");
			}
			OperationSet os = (OperationSet) o;
			os.addAllInherited(set);
		} else {
			//Op set not present, create new one
			contentSet.put(name, createInheritedOperationSet(set));
		}
	}

	@Override
	protected ScopedElement doHandleDuplicate(ScopedElement oldElem, ScopedElement newElem) {
		// Both methods defined in the same scope? Fail!
		boolean oldInherited = isElementInherited(oldElem);
		boolean newInherited = isElementInherited(newElem);
		
		//Both inherited?
		if(oldInherited && newInherited){
			//TODO: Multiple inheritance handling
			throw new Error("Inheritance from two types?");
		} else if(oldInherited) {
			return handleInheritance(oldElem,newElem);
		} else if(newInherited) {
			return handleInheritance(newElem,oldElem);
		} else {
			//None of them is inherited, duplicate!
			throw Problem.ofType(ProblemId.DUPLICATE_NAME).at(newElem.getDefinition(),oldElem.getDefinition())
			.details(newElem.getUid())
			.raiseUnrecoverable();
		}
		
	
	}

	/**
	 * Checks about shadowed fields
	 * @param superElem
	 * @param subElem
	 */
	private ScopedElement handleInheritance(ScopedElement superElem, ScopedElement subElem) {
		//Different types? Fail
		if(superElem.getElementType() != subElem.getElementType()){
			//FIXME Proper problem handling
			throw new Error("Incompatible override");
		}
		
		//Shadowing permitted
		return subElem;
	}

	

}
