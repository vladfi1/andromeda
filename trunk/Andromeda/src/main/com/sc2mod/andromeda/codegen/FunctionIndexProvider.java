package com.sc2mod.andromeda.codegen;

import java.util.HashMap;
import java.util.Iterator;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.scopes.content.TraversalPolicies;
import com.sc2mod.andromeda.environment.types.IClass;

/**
 * Does the indexing which creates indices for methods with the same name.
 * Saves the indices for later and provides them with the getFuncIndex method.
 * @author gex
 *
 */
public class FunctionIndexProvider {

	private HashMap<Operation, Integer> indices = new HashMap<Operation, Integer>();
	
	public FunctionIndexProvider(Environment env){
		//Normal methods.
		for(Iterator<IScopedElement> it = env.iterateOverContent(TraversalPolicies.OP_SETS_ONLY).iterator(); it.hasNext();){
			IScopedElement elem = it.next();
			indexOpSet((OperationSet)elem);
		}
		
		//Constructors.
		for(IClass c : env.typeProvider.getClasses()){
			indexOpSet(c.getConstructors());
		}
		
	}
	private void indexOpSet(OperationSet elem) {
		if(elem.size() < 2) return;
		int index = 0;
		for(Operation op : elem){
			if(index != 0){
				indices.put(op, index);
			}
			index++;
		}
	}
	
	public int getFuncIndex(Function f){
		Integer i = indices.get(f);
		if(i == null) return 0;
		return i.intValue();
	}
}
