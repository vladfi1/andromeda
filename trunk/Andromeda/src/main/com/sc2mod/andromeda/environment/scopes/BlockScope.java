package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.scopes.content.InheritableContentSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;

public class BlockScope extends ScopeImpl {

	private IScope parentScope;
	protected BlockScope(IScope parentScope){
		this.parentScope = parentScope;
	}
	/**
	 * For types, only add the content to the scope itself, not any parent
	 * scopes.
	 */
	@Override
	public void addContent(String name, IScopedElement elem){
		getContent().add(name, elem);
	}

	@Override
	public Package getPackage() {
		return parentScope.getPackage();
	}

	@Override
	public IScope getParentScope() {
		return parentScope;
	}

	@Override
	protected ScopeContentSet createContentSet() {
		return new InheritableContentSet(this);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
