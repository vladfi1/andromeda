package com.sc2mod.andromeda.environment.types.generic;

import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.GenericMethodProxy;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.GenericVarProxy;
import com.sc2mod.andromeda.environment.variables.Variable;


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
				OperationSet opSet = (OperationSet)elem;
				for(Operation op : opSet){
					copyTo.add(key, getGenericOperation(tprov,op,typeArguments));
				}
				break;
			case VAR:
				copyTo.add(key,getGenericVarDecl(tprov,(Variable)elem, typeArguments));
				break;
			case TYPE:
				//type parameters are not copied down
				if(((IType)elem).getCategory() == TypeCategory.TYPE_PARAM)
					break;
			default:
				copyTo.add(key,elem);
			}
			
		}
		
	}

	public static Operation getGenericOperation(TypeProvider tprov,
			Operation op, Signature typeArguments) {
		IType t = op.getReturnType();
		IType t2 = tprov.insertTypeArgs(t, typeArguments);
		Signature oldSignature = op.getSignature();
		Signature newSignature = tprov.insertTypeArgsInSignature(oldSignature, typeArguments);
		return new GenericMethodProxy(op,newSignature,t2);
	}

	private static Variable getGenericVarDecl(TypeProvider tprov, Variable elem, Signature typeArguments) {
		IType t = elem.getType();
		IType t2 = tprov.insertTypeArgs(t, typeArguments);
		return new GenericVarProxy(elem,t2);
	}

	

}
