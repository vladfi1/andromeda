package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;

public abstract class ScopeImpl implements IScope , SemanticsVisitorNode {

	
	protected ScopeImpl(){
		this(false);
	}
	
	protected ScopeImpl(boolean mayHaveDuplicates){
		content = createContentSet();
	}
	
	public ScopeContentSet getContent(){
		return content;
	}
	
	private ScopeContentSet content;
	
	/**
	 * Adds content to this scope. Also adds
	 * the content recursively to the parent scopes, if the visibility
	 * allows it. Since visibility is handled differently, depending
	 * on the type of the scope, this method is abstract.
	 * @param name
	 * @param elem
	 */
	public abstract void addContent(String name, IScopedElement elem);
	

	protected abstract ScopeContentSet createContentSet();

	protected void recreateContentSet(){
		if(content == null){
			content = createContentSet();
		}
	}
}

