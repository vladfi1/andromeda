package com.sc2mod.andromeda.environment.access;

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;

public class TypeAccess extends NameAccess {

	private IType type;
	public TypeAccess(IType whichType){
		this.type = whichType;
	}

	@Override
	public AccessType getAccessType() {
		return AccessType.TYPE;
	}

	@Override
	public IType getAccessedElement() {
		return type;
	}

	@Override
	public IScope getPrefixScope() {
		return type;
	}

	@Override
	public boolean isStatic() {
		return type.isStaticElement();
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
