package com.sc2mod.andromeda.environment.types.generic;

import java.util.HashSet;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;

public class GenericInterfaceInstance extends GenericTypeInstance implements IInterface{

	private IInterface genericParent;

	public GenericInterfaceInstance(IInterface i, Signature scope, TypeProvider t) {
		super(i, scope, t);
		this.genericParent = i;
	}
	
	@Override
	public IInterface getGenericParent() {
		return genericParent;
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
	public InterfaceDeclNode getDefinition() {
		return genericParent.getDefinition();
	}
	

	@Override
	public int getIndex() {
		return genericParent.getIndex();
	}

	@Override
	public int getTableIndex() {
		return genericParent.getTableIndex();
	}

	@Override
	public HashSet<IInterface> getInterfaces() {
		return genericParent.getInterfaces();
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
