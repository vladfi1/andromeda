package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

public abstract class NonLocalVarDecl extends NonParamDecl{

	public NonLocalVarDecl(FieldDeclNode f,
			VarDeclNode declNode, IScope scope) {
		super(f.getFieldModifiers(),f.getType(),declNode,scope);
	}
	
	@Override
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
}
