package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.types.IExtension;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;

public class GenericExtensionInstance extends GenericTypeInstance implements IExtension {

	private final IExtension genericParent;

	public GenericExtensionInstance(IExtension i, Signature s, TypeProvider t){
		super(i,s,t);
		this.genericParent = i;
		
	}
	
	@Override
	public IExtension getGenericParent() {
		return genericParent;
	}
	

	@Override
	public int getExtensionHierachryLevel() {
		return genericParent.getExtensionHierachryLevel();
	}

	@Override
	public boolean isDistinct() {
		return genericParent.isDistinct();
	}

	@Override
	public boolean isKey() {
		return genericParent.isKey();
	}

	@Override
	public void setResolvedExtendedType(IType extendedType2,
			BasicType extendedBaseType2, int hierarchyLevel2) {
		throw new Error("Not allowed!");
	}
	
	@Override
	public BasicType getBaseType() {
		return genericParent.getBaseType();
	}
	
	@Override
	public TypeExtensionDeclNode getDefinition() {
		return genericParent.getDefinition();
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
