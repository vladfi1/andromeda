/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.scopes.content.NonInheritanceContentSet;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.parsing.InclusionType;

public class FileScope extends ScopeImpl {

	
	private Package pkg;
	private InclusionType inclusionType;
	private String name;
	
	public FileScope(String fileName, InclusionType inclusionType,Package pkg){
		this.name = fileName;
		this.inclusionType = inclusionType;
		this.pkg = pkg;
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + name + ")";
	}

	public InclusionType getInclusionType() {
		return inclusionType;
	}

	public void setInclusionType(InclusionType inclusionType) {
		this.inclusionType = inclusionType;
	}


	@Override
	public Package getPackage() {
		return pkg;
	}

	@Override
	public IScope getParentScope() {
		return pkg;
	}


	/**
	 * For files, the content is only added to the parent scope (package)
	 * if it is not private
	 */
	@Override
	public void addContent(String name, IScopedElement elem){
		getContent().add(name, elem);
		IScope parentScope = getParentScope();
		if(parentScope != null && elem.getVisibility()!=Visibility.PRIVATE) parentScope.addContent(name, elem);
	}
	
	@Override
	protected ScopeContentSet createContentSet() {
		return new NonInheritanceContentSet(this);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
