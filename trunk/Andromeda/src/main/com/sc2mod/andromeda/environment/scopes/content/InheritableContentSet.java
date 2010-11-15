package com.sc2mod.andromeda.environment.scopes.content;

import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class InheritableContentSet extends ScopeContentSet {

	public InheritableContentSet(IType scope) {
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
	public boolean isElementInherited(IScopedElement value) {
		return value.getContainingType()!=scope;
	}
	
	public void addInheritedContent(ScopeContentSet parentSet) {
		for(Entry<String, IScopedElement> e : parentSet.getContentSet().entrySet()){
			IScopedElement elem = e.getValue();
			
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
		IScopedElement o = getContentSet().get(name);
		if(o != null){
			//Op set present, add the method to it
			Operation op = set.getAny();
			if(o.getElementType() != ScopedElementType.OP_SET){
				throw Problem.ofType(ProblemId.OVERRIDE_FORBIDDEN_ELEMENT).at(o.getDefinition())
				.details(op.getElementTypeName(),op.toString(),o.getElementTypeName())
				.raiseUnrecoverable();
			}
			OperationSet os = (OperationSet) o;
			os.addAllInherited(set);
		} else {
			//Op set not present, create new one
			getContentSet().put(name, createInheritedOperationSet(set));
		}
	}

	@Override
	protected IScopedElement doHandleDuplicate(IScopedElement oldElem, IScopedElement newElem) {
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
	private IScopedElement handleInheritance(IScopedElement superElem, IScopedElement subElem) {
		//Different types? Fail
		if(superElem.getElementType() != subElem.getElementType()){
			SyntaxNode[] definitions;
			if(subElem.getElementType() == ScopedElementType.OP_SET){
				OperationSet opSet = ((OperationSet)subElem);
				definitions = new SyntaxNode[opSet.size()];
				int i = 0;
				for(Operation op : opSet){
					definitions[i++]=op.getDefinition();
					subElem = op;
				}
			} else {
				definitions = new SyntaxNode[]{subElem.getDefinition()};
			}
			
			throw Problem.ofType(ProblemId.OVERRIDE_FORBIDDEN_ELEMENT).at(definitions)
				.details(superElem.getElementTypeName(),superElem.toString(),subElem.getElementTypeName())
				.raiseUnrecoverable();
		}
		
		//Shadowing permitted
		return subElem;
	}

	

}
