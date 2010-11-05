package com.sc2mod.andromeda.environment.types.generic;

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
		
		genericClassInstance.setConstructors(GenericUtil.getGenericOperationSet(tprov,genericClassInstance.getConstructors(), genericClassInstance.getTypeArguments()));
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
