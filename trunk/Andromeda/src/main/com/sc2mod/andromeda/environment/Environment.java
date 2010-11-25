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

import com.sc2mod.andromeda.environment.annotations.AnnotationRegistry;
import com.sc2mod.andromeda.environment.scopes.GlobalScope;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.parsing.Language;

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
	


	public Environment(Language lang){
		typeProvider = new TypeProvider(this,lang);
	}

	private GlobalScope theGlobalScope = new GlobalScope();
	private Package defaultPackage = new Package(this, "<default>", null);
	public final TypeProvider typeProvider;
	public final AnnotationRegistry annotationRegistry = new AnnotationRegistry();
	
	public Iterable<IScopedElement> iterateOverContent(final boolean stepIntoOperations,final boolean stepIntoPackages){
		return new Iterable<IScopedElement>() {
			
			@Override
			public Iterator<IScopedElement> iterator() {
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
