package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

public abstract class NonLocalVarDecl extends NonParamDecl{
	
	private final int index;

	public NonLocalVarDecl(FieldDeclNode f,
			VarDeclNode declNode, IScope scope, Environment env) {
		super(f.getFieldModifiers(),f.getType(),declNode,scope);
		index = env.getIdProvider().getNextGlobalVarId();
	}
	
	@Override
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	
	@Override
	public int getDeclarationIndex() {
		return index;
	}
	
	
}
