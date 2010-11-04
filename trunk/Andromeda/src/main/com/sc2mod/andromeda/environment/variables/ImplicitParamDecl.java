package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;

public class ImplicitParamDecl extends ParamDecl {

	public ImplicitParamDecl(IType type, String name) {
		super(null, type, new IdentifierNode(name));
	}

}
