/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
/**
 * A variable stack is a datastructure that is able to register local
 * and global variables and then get the declaration of a variable
 * by its name.
 * Since I am not sure if an array with linear search or a hashmap
 * is better faster for this purpose, I have designed this as an abstract
 * class and will test both implementations as subclasses.
 * @author gex
 */
public abstract class LocalVarStack {
	
	public abstract void addLocalVar(VarDecl decl);
	
	public abstract void pushLocalBlock();
	
	public abstract void popLocalBlock();
	
	public abstract VarDecl resolveVar(String name);
	
	public abstract LocalVarDecl[] methodFinished(int numParams);
}
