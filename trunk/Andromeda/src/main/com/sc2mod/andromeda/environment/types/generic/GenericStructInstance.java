package com.sc2mod.andromeda.environment.types.generic;

import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;

public class GenericStructInstance extends GenericTypeInstance implements IStruct{

	private IStruct genericParent;

	public GenericStructInstance(IStruct struct, Signature s, TypeProvider t) {
		super(struct,s, t);
		this.genericParent = struct;
	}


	@Override
	public int calcByteSize() {
		return genericParent.calcByteSize();
	}

	@Override
	public LinkedList<IRecordType> getDescendants() {
		return genericParent.getDescendants();
	}
	
	@Override
	public StructDeclNode getDefinition() {
		return genericParent.getDefinition();
	}

	
	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
