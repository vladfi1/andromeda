package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;

/**
 * A scope, also called namespace, is a part of code
 * that contains scoped elements.
 * Scopes are necessary for the following things:
 * - Resolving names
 * - Checking visibility: i.e. if a name in a scope can be accessed from another scope
 * 
 * Examples for scope:
 * A source file (FileScope)
 * A block (BlockScope), i.e. a class, enrichment, interface,...
 * A package (PackageScope)
 * The global scope (GlobalScope) that contains all public global names
 * @author gex
 *
 */
public interface IScope extends SemanticsElement {

	
	IScope getParentScope();
	Package getPackage();
	
	ScopeContentSet getContent();
	
	/**
	 * Adds content to this scope. Also adds
	 * the content recursively to the parent scopes, if the visibility
	 * allows it. Since visibility is handled differently, depending
	 * on the type of the scope, this method is abstract.
	 * @param name
	 * @param elem
	 */
	void addContent(String name, IScopedElement elem);
	


	
}

