package com.sc2mod.andromeda.environment.scopes;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.scopes.content.NonInheritanceContentSet;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

//FIXME package testcases
public class Package extends ScopeImpl implements IScopedElement {

	private Package parent;
	private String name;
	private Environment env;
	private Map<String, Package> subPackages = new HashMap<String, Package>();
	
	
	public Package(Environment env, String name, Package parent){
		this.name = name;
		this.parent = parent;
		this.env = env;
	}
	
	public Package getParentPackage(){
		return parent;
	}
	
	public boolean isSubpackageOf(Package otherPackage){
		if(otherPackage == null) return false;
		if(this == otherPackage) return true;
		Package parent = getParentPackage();
		if(parent == null) return false;
		return parent.isSubpackageOf(otherPackage);
	}
	
	public Package resolveSubpackage(String name, SyntaxNode where){
		return subPackages.get(name);
	}
	
	
	public Package addOrGetSubpackage(String name, SyntaxNode where){
		Package result = subPackages.get(name);
		if(result != null) return result;

		//Subpackage not present yet. Create it.
		result = new Package(env, name, parent);
		subPackages.put(name, result);
		return result;
	}

	@Override
	public Package getPackage() {
		return this;
	}
	
	@Override
	public String toString() {
		return "package " + name;
	}

	/**
	 * The parent scope of a package is always the global one,
	 * since packages are the last step in the lookup hierarchy
	 * before the globally unique scope is checked as a final step.
	 * 
	 * The global scope of a package is currently not its parent
	 * package! This means that you have to import classes from
	 * parent packages, since packages are not looked up recursively.
	 */
	@Override
	public IScope getParentScope() {
		return env.getTheGlobalScope();
	}

	@Override
	public ScopedElementType getElementType() {
		return ScopedElementType.PACKAGE;
	}

	@Override
	public IScope getScope() {
		return getParentPackage();
	}

	@Override
	public Visibility getVisibility() {
		return Visibility.PUBLIC;
	}

	@Override
	public boolean isStaticElement() {
		return true;
	}

	@Override
	public String getUid() {
		return name;
	}

	@Override
	public SyntaxNode getDefinition() {
		throw new InternalProgramError("Cannot get the defintion of a package");
	}


	/**
	 * Only add content also to the parent, if it is public
	 */
	@Override
	public void addContent(String name, IScopedElement elem) {
		getContent().add(name, elem);

		IScope parentScope = getParentScope();
		if(parentScope != null && elem.getVisibility()==Visibility.PUBLIC) parentScope.addContent(name, elem);
		
	}

	@Override
	protected ScopeContentSet createContentSet() {
		return new NonInheritanceContentSet(this);
	}
	
	@Override
	public IType getContainingType() {
		return null;
	}
	
	@Override
	public String getElementTypeName() {
		return "package";
	}
	




	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }

	public Iterator<? extends IScopedElement> subpackageIterator() {
		return subPackages.values().iterator();
	}
}
