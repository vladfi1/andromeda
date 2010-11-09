package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;

import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class ImplicitParamDecl extends ParamDecl {

	public ImplicitParamDecl(IType type, String name) {
		super(null, type, new IdentifierNode(name));
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
