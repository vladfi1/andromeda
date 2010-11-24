package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.types.ArrayType;
import com.sc2mod.andromeda.environment.types.ClosureType;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;
import com.sc2mod.andromeda.problems.ErrorUtil;

public class TypeParamInstanciationVisitor extends ParameterSemanticsVisitorAdapter<Signature, IType>{
	
	private TypeProvider tprov;
	public TypeParamInstanciationVisitor(TypeProvider typeProvider) {
		tprov = typeProvider;
	}



	/**
	 * Type parameters get replaced
	 */
	@Override
	public IType visit(TypeParameter typeParameter, Signature state) {
		return state.get(typeParameter.getIndex());
	}
	

	
	//*********************************************************
	//All named types can contain the type parameters if they are generic
	//*********************************************************

	
	@Override
	public IType visit(GenericClassInstance genericClassInstance,
			Signature state) {
		return checkGeneric(genericClassInstance, state);
	}
	
	@Override
	public IType visit(GenericExtensionInstance genericExtensionInstance,
			Signature state) {
		return checkGeneric(genericExtensionInstance, state);
	}
	
	@Override
	public IType visit(GenericStructInstance genericStructInstance,
			Signature state) {
		return checkGeneric(genericStructInstance, state);
	}
	
	@Override
	public IType visit(GenericInterfaceInstance genericInterfaceInstance,
			Signature state) {
		return checkGeneric(genericInterfaceInstance, state);
	}
	
	private IType checkGeneric(INamedType type, Signature typeArguments) {
		if(!type.isGenericInstance())
			return type;
		
		Signature args = type.getTypeArguments();
		Signature newSig = tprov.insertTypeArgsInSignature(args, typeArguments);
		if(newSig == args)
			return type;
		return tprov.getGenericInstance(type, newSig);
	}



	//Arrays can contain the parameter
	public IType visit(ArrayType arrayType, Signature state) {
		IType innerTypeBefore = arrayType.getWrappedType();
		IType innerTypeAfter = innerTypeBefore.accept(this,state);
		if(innerTypeBefore == innerTypeAfter){
			return arrayType;
		}
		
		return tprov.getArrayType(innerTypeAfter, arrayType.getDimension());
		
	}
	
	//Function pointers can contain the parameter as signature or return type
	@Override
	public IType visit(ClosureType functionPointer, Signature state) {
		// TODO Implement it for function pointers
		throw new Error("Not implemented!");
	}
	
	
	@Override
	public IType visitDefault(SemanticsElement semanticsElement,
			Signature state) {
		if(!(semanticsElement instanceof IType)){
			throw ErrorUtil.defaultInternalError();
		}
		return (IType) semanticsElement;
	}
}
