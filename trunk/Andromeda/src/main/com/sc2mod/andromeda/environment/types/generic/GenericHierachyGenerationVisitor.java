package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;

public class GenericHierachyGenerationVisitor extends VoidSemanticsVisitorAdapter {
	
	private TypeProvider tprov;

	GenericHierachyGenerationVisitor(TypeProvider tprov){
		this.tprov = tprov;
	}

	
	private void setGenericSuperType(GenericTypeInstance i){
		IType t = i.getSuperType();
		if(t != null)
			i.setGenericSuperType(tprov.insertTypeArgs(t, i.getTypeArguments()));
	}

	@Override
	public void visit(GenericClassInstance genericClassInstance) {

		genericClassInstance.setGenericHierarchyCopied(true);
		setGenericSuperType(genericClassInstance);
		//TODO: Generic super interface type argument insertion
	}
	
	@Override
	public void visit(GenericExtensionInstance genericExtensionInstance) {

		genericExtensionInstance.setGenericHierarchyCopied(true);
		setGenericSuperType(genericExtensionInstance);

	}
	
	@Override
	public void visit(GenericInterfaceInstance genericInterfaceInstance) {
		//TODO: Generic super interface type argument insertion

		genericInterfaceInstance.setGenericHierarchyCopied(true);
	}
	
	@Override
	public void visit(GenericStructInstance genericStructInstance) {

		genericStructInstance.setGenericHierarchyCopied(true);
	}
	
	
}
