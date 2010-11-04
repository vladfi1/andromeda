package com.sc2mod.andromeda.environment.scopes.content;

import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.GenericMethodProxy;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.environment.variables.GenericVarProxy;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.InternalProgramError;

public class GenericContentSet extends ScopeContentSet{

	private ScopeContentSet genericParentSet;
	private TypeParamMapping paramMapping;
	public GenericContentSet(ScopeContentSet parentSet, TypeParamMapping paramMapping) {
		super(parentSet.scope);
		genericParentSet = parentSet;
		this.paramMapping = paramMapping;
	}

	@Override
	protected OperationSet createInheritedOperationSet(OperationSet copyFrom) {
		throw new InternalProgramError("Cannot call this method for generic content sets");
	}

	@Override
	protected OperationSet createOperationSet(Operation start) {
		throw new InternalProgramError("Cannot call this method for generic content sets");
	}

	@Override
	protected IScopedElement doHandleDuplicate(IScopedElement oldElem,
			IScopedElement newElem) {
		throw new InternalProgramError("Cannot call this method for generic content sets");
	}
	
	public void copyFromGenericParent(){
		for(Entry<String, IScopedElement> e: genericParentSet.viewEntries()){
			String key = e.getKey();
			IScopedElement elem = e.getValue();
			
			switch(elem.getElementType()){
			case OP_SET: 
				add(key,getGenericOperationSet((OperationSet)elem, paramMapping));
				break;
			case VAR:
				addGenericVarDecl(key,(VarDecl)elem);
				break;
			default:
				add(key,elem);
			}
			
		}
		
	}

	private void addGenericVarDecl(String key, VarDecl elem) {
		IType t = elem.getType();
		IType t2 = t.replaceTypeParameters(paramMapping);
		
		if(t != t2){
			//Type has changed we need a generic proxy
			add(key, new GenericVarProxy(elem,t2));
		} else {
			//Type has not changed, just use the old field
			add(key, elem);
		}
	}
	

	/**
	 * Generates an operation set from another operation set by applying
	 * a type parameter mapping. This is used for generic types.
	 * 
	 * If no operation must be replaced (all operations in the op set do not use the
	 * type parameters in the mapping), the operation set itself is returned. Otherwise,
	 * a newly created set is returned.
	 * @param from
	 * @param mapping
	 * @return
	 */
	public static OperationSet getGenericOperationSet(OperationSet from, TypeParamMapping mapping){
		//Check if we need to create a new op set
		boolean mustReplace = false;
		for(Operation op: from){
			
			IType t = op.getReturnType();
			IType t2 = t.replaceTypeParameters(mapping);
			Signature oldSignature = op.getSignature();
			Signature newSignature = oldSignature.replaceTypeParameters(mapping);
			
			
			if(t != t2 || newSignature.equals(oldSignature)){
				//We have a method that needs to be replaced!
				mustReplace = true;
				break;
			}
		}
		
		//Nothing to replace, just return the old opset
		if(!mustReplace){
			return from;
		}
		
		//If we need to, do the replacement
		OperationSet newSet = new MethodSet(from.getScope(), from.getUid());
		for(Operation op: from){
			
			IType t = op.getReturnType();
			IType t2 = t.replaceTypeParameters(mapping);
			Signature oldSignature = op.getSignature();
			Signature newSignature = oldSignature.replaceTypeParameters(mapping);
			
			if(t != t2 || newSignature.equals(oldSignature)){
				//Type has changed we need a generic proxy
				newSet.add(new GenericMethodProxy(op,newSignature,t2));
			} else {
				//Type has not changed, just use the old field
				newSet.add(op);
			}
		}
		
		return newSet;
	}

	
}
