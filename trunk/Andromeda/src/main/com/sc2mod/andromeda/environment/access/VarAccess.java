package com.sc2mod.andromeda.environment.access;

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;

public class VarAccess extends NameAccess {
	
	private Variable var;
	
	public VarAccess(Variable var) {
		this.var = var;
	}
	
	@Override
	public Variable getAccessedElement() {
		return var;
	}

	@Override
	public AccessType getAccessType() {
		return AccessType.VAR;
	}

	@Override
	public IScope getPrefixScope() {
		return var.getType();
	}

	@Override
	public boolean isStatic() {
		return var.isStaticElement();
	}
	


	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
