package com.sc2mod.andromeda.environment.types;

import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class TypeError extends Type {
	
	public static final TypeError INSTANCE = new TypeError();

	private TypeError() {
		super(null);
	}

	@Override
	public int getByteSize() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public TypeCategory getCategory() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public int getRuntimeType() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public Visibility getVisibility() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public String getUid() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public SyntaxNode getDefinition() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

}