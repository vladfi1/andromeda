package com.sc2mod.andromeda.environment.types.basic;

import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.impl.TypeImpl;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class TypeError extends TypeImpl {
	
	//public static final TypeError INSTANCE = new TypeError();

	private TypeError(TypeProvider t) {
		super(null,t);
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
