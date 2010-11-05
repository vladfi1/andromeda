package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.types.ArrayType;
import com.sc2mod.andromeda.environment.types.ClosureType;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;

public class TypeParamIdentificationVisitor extends ParameterSemanticsVisitorAdapter<Void, Boolean>{

	/**
	 * Type parameters do contain type parameters, literally
	 */
	@Override
	public Boolean visit(TypeParameter typeParameter, Void state) {
		return true;
	}
	

	
	//*********************************************************
	//All named types can contain the type parameters if they are generic
	//*********************************************************
	
	@Override
	public Boolean visit(ExtensionImpl extensionImpl, Void state) {
		return checkGeneric(extensionImpl, state);
	}
	
	@Override
	public Boolean visit(StructImpl structImpl, Void state) {
		return checkGeneric(structImpl, state);
	}
	
	@Override
	public Boolean visit(ClassImpl classImpl, Void state) {
		return checkGeneric(classImpl, state);
	}
	
	@Override
	public Boolean visit(InterfaceImpl interfaceImpl, Void state) {
		return checkGeneric(interfaceImpl, state);
	}
	
	@Override
	public Boolean visit(GenericClassInstance genericClassInstance,
			Void state) {
		return checkGeneric(genericClassInstance, state);
	}
	
	@Override
	public Boolean visit(GenericExtensionInstance genericExtensionInstance,
			Void state) {
		return checkGeneric(genericExtensionInstance, state);
	}
	
	@Override
	public Boolean visit(GenericStructInstance genericStructInstance,
			Void state) {
		return checkGeneric(genericStructInstance, state);
	}
	
	@Override
	public Boolean visit(GenericInterfaceInstance genericInterfaceInstance,
			Void state) {
		return checkGeneric(genericInterfaceInstance, state);
	}
	
	private Boolean checkGeneric(INamedType type, Void typeArguments) {
		if(!type.isGenericInstance())
			return false;
		
		Signature args = type.getTypeArguments();
		
		//If the signature has type arguments, then this type has also
		return args.containsTypeParams();
	}



	//Arrays can contain the parameter
	public Boolean visit(ArrayType arrayType, Void state) {
		return arrayType.getWrappedType().accept(this,null);
		
	}
	
	//Function pointers can contain the parameter as signature or return type
	@Override
	public Boolean visit(ClosureType functionPointer, Void state) {
		return functionPointer.getReturnType().accept(this,null) || functionPointer.getSignature().containsTypeParams();
	}
	
	
	@Override
	public Boolean visitDefault(SemanticsElement semanticsElement,
			Void state) {
		return false;
	}
}
