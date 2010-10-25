package com.sc2mod.andromeda.environment.scopes;

import java.util.EnumSet;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.notifications.InternalProgramError;

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
public abstract class Scope extends SemanticsElement {

	
	protected Scope(){
		this(false);
	}
	
	protected Scope(boolean mayHaveDuplicates){
		content = createContentSet();
	}
	
	protected abstract ScopeContentSet createContentSet();
	
	public abstract Scope getParentScope();
	public abstract Package getPackage();
	
	public ScopeContentSet getContent(){
		return content;
	}
	private final ScopeContentSet content;
	
	/**
	 * Adds content to this scope. Also adds
	 * the content recursively to the parent scopes, if the visibility
	 * allows it. Since visibility is handled differently, depending
	 * on the type of the scope, this method is abstract.
	 * @param name
	 * @param elem
	 */
	public abstract void addContent(String name, ScopedElement elem);
	


	
}

