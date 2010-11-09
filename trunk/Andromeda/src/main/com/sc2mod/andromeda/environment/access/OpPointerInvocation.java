package com.sc2mod.andromeda.environment.access;

import com.sc2mod.andromeda.environment.variables.VarDecl;

import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class OpPointerInvocation extends Invocation {

	public OpPointerInvocation(VarDecl invokedVar) {
		super(null, InvocationType.CLOSURE);
		// TODO Auto-generated constructor stub
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
