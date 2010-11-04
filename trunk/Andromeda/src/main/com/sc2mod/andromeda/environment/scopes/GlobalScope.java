package com.sc2mod.andromeda.environment.scopes;

/**
 * The global scope is unique and contains ALL public members from ALL
 * included files. It is the only scope that can have
 * more than one member with the same name (However, trying
 * to access those members will raise an error). This is because
 * there might be two elements with the same name in different packages.
 * In contrast to java, for simplicity reasons, Andromeda allows
 * accessing an element that was defined anywhere without importing it,
 * if the name is unique in all packages. If the name is not unique
 * the user will get an error upon accessing such an element. He then
 * must import one of the names, to avoid the name clash.
 * 
 * Do not mix up the global scope with the default unnamed package!
 * The default unnamed package contains only public members of files
 * that have no package declaration. It is a normal package scope like
 * any other package (and may not have duplicates for example).
 * 
 * @author gex
 *
 */
import com.sc2mod.andromeda.environment.scopes.content.GlobalContentSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class GlobalScope extends ScopeImpl {

	/**
	 * Creates a global scope. Should only be created
	 * by the environment once per compilation!
	 */
	public GlobalScope() {
		//We use super(true) to get a scope content set that
		//allows duplicates.
		super(true);
	}
	
	@Override
	public Package getPackage() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public IScope getParentScope() {
		return null;
	}

	
	/**
	 * Just add it to this content and no recursive call,
	 * the global scope has no ancestor anyway.
	 */
	@Override
	public void addContent(String name, IScopedElement elem){
		getContent().add(name, elem);
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	@Override
	protected ScopeContentSet createContentSet() {
		return new GlobalContentSet(this);
	}
	
	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
