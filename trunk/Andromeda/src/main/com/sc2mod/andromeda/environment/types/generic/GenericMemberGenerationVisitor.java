package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;

public class GenericMemberGenerationVisitor extends VoidSemanticsVisitorAdapter {
	
	private TypeProvider tprov;

	public GenericMemberGenerationVisitor(TypeProvider tprov){
		this.tprov = tprov;
	}
	
	private void copyGenericContent(GenericTypeInstance instance){
		GenericUtil.copyContentFromGenericParent(tprov,instance.getContent(), instance.getGenericParent().getContent(), instance.getTypeArguments());
	}

	@Override
	public void visit(GenericClassInstance genericClassInstance) {
		copyGenericContent(genericClassInstance);
		
		Signature typeArgs = genericClassInstance.getTypeArguments();
		OperationSet constructors = genericClassInstance.getConstructors();
		for(Operation op : genericClassInstance.getGenericParent().getConstructors()){
			constructors.add(GenericUtil.getGenericOperation(tprov, op, typeArgs));
		}
	}
	
	@Override
	public void visit(GenericExtensionInstance genericExtensionInstance) {
		copyGenericContent(genericExtensionInstance);
	}
	
	@Override
	public void visit(GenericInterfaceInstance genericInterfaceInstance) {
		copyGenericContent(genericInterfaceInstance);
	}
	
	@Override
	public void visit(GenericStructInstance genericStructInstance) {
		copyGenericContent(genericStructInstance);
	}
	
}
