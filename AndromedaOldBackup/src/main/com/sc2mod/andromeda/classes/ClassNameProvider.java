/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.classes;

import com.sc2mod.andromeda.environment.types.Class;
public abstract class ClassNameProvider {

	protected Class clazz;
	protected String memoryName;
	
	public String getMemoryName() {
		return memoryName;
	}

	public void setMemoryName(String memoryName) {
		this.memoryName = memoryName;
	}

	public ClassNameProvider(Class c){
		this.clazz = c;
	}
	
	
	
	public abstract void setName(String name);
	
	public abstract String getName();	
	
	public abstract String getStructName();
}
