package com.sc2mod.andromeda.environment.scopes.content;

import java.util.Iterator;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.generic.GenericTypeInstance;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class GenericConstructorSet extends MethodSet {

	private GenericTypeInstance type;
	

	
	public GenericConstructorSet(IScope owner, String uid, GenericTypeInstance type) {
		super(owner,uid);
		this.type = type;
	}

	@Override
	public Operation get(Signature s, SyntaxNode where, IScope from) {
		throw new Error("Not permitted!");
	}

	@Override
	public Operation get(Signature s, SyntaxNode where) {
		
		throw new Error("Not permitted!");
	}

	@Override
	public Operation getAny() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public IType getContainingType() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public SyntaxNode getDefinition() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public ScopedElementType getElementType() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public String getElementTypeName() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public IScope getScope() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public String getUid() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public Visibility getVisibility() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public boolean isEmpty() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public boolean isStaticElement() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public Iterator<Operation> iterator() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public int size() {
		
		throw new Error("Not permitted!");
	}

	@Override
	public Iterable<Entry<Signature, Operation>> viewEntries() {
		
		throw new Error("Not permitted!");
	}
	
	

}
