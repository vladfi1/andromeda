/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.Iterator;

import com.sc2mod.andromeda.environment.scopes.GlobalScope;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.ScopedElement;
import com.sc2mod.andromeda.environment.types.TypeProvider;

/**
 * The semantics environment is the root for the semantics of a syntax tree.
 * It contains the default package as the root of the package hierarchy and
 * the global scope for resolving globally public elements.
 * It also contains the type provider which contains additional information
 * about types in the environment.
 * @author gex
 *
 */
public final class Environment {

	private GlobalScope theGlobalScope = new GlobalScope();
	private Package defaultPackage = new Package(this, "<default>", null);
	public final TypeProvider typeProvider = new TypeProvider(this);
	
	public Iterable<ScopedElement> iterateOverContent(final boolean stepIntoOperations,final boolean stepIntoPackages){
		return new Iterable<ScopedElement>() {
			
			@Override
			public Iterator<ScopedElement> iterator() {
				return defaultPackage.getContent().getDeepIterator(stepIntoOperations, stepIntoPackages);
			}
		};
	}


	/**
	 * Returns the globally unique scope that
	 * contains all public members of all included files.
	 * @see GlobalScope
	 * @return the unique global scope.
	 */
	public GlobalScope getTheGlobalScope() {
		return theGlobalScope;
	}
	
	public Package getDefaultPackage() {
		return defaultPackage;
	}


}
