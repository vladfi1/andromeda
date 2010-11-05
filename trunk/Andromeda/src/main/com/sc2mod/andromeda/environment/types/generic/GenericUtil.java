package com.sc2mod.andromeda.environment.types.generic;

import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.GenericMethodProxy;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.content.MethodSet;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.GenericVarProxy;
import com.sc2mod.andromeda.environment.variables.VarDecl;


public final class GenericUtil {

	/**
	 * Util class
	 */
	private GenericUtil(){}
	
	public static void copyContentFromGenericParent(TypeProvider tprov, ScopeContentSet copyTo, ScopeContentSet genericParentSet, Signature typeArguments){
		for(Entry<String, IScopedElement> e: genericParentSet.viewEntries()){
			String key = e.getKey();
			IScopedElement elem = e.getValue();
			
			switch(elem.getElementType()){
			case OP_SET: 
				copyTo.add(key,getGenericOperationSet(tprov,(OperationSet)elem, typeArguments));
				break;
			case VAR:
				copyTo.add(key,getGenericVarDecl(tprov,(VarDecl)elem, typeArguments));
				break;
			default:
				copyTo.add(key,elem);
			}
			
		}
		
	}

	private static VarDecl getGenericVarDecl(TypeProvider tprov, VarDecl elem, Signature typeArguments) {
		IType t = elem.getType();
		IType t2 = tprov.insertTypeArgs(t, typeArguments);
		
		if(t != t2){
			//Type has changed we need a generic proxy
			return new GenericVarProxy(elem,t2);
		} else {
			//Type has not changed, just use the old field
			return elem;
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
	public static OperationSet getGenericOperationSet(TypeProvider tprov, OperationSet from, Signature typeArguments){
		//Check if we need to create a new op set
		boolean mustReplace = false;
		for(Operation op: from){
			
			IType t = op.getReturnType();
			IType t2 = tprov.insertTypeArgs(t, typeArguments);
			Signature oldSignature = op.getSignature();
			Signature newSignature = tprov.insertTypeArgsInSignature(oldSignature, typeArguments);
			
			
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
			IType t2 = tprov.insertTypeArgs(t, typeArguments);
			Signature oldSignature = op.getSignature();
			Signature newSignature = tprov.insertTypeArgsInSignature(oldSignature, typeArguments);
			
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
