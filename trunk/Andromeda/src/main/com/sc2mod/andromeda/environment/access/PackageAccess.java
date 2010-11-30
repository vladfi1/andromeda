package com.sc2mod.andromeda.environment.access;

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;

public class PackageAccess extends NameAccess {

	private Package pkg;
	public PackageAccess(Package pkg){
		this.pkg = pkg;
	}

	@Override
	public AccessType getAccessType() {
		return AccessType.PACKAGE;
	}

	@Override
	public Package getAccessedElement() {
		return pkg;
	}

	@Override
	public IScope getPrefixScope() {
		return pkg;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
