/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.classes.indexSys;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.IClass;

public class IndexClassNameProvider extends ClassNameProvider{

	private String structName;
	public IndexClassNameProvider(IClass c) {
		super(c);
	}

	@Override
	public String getName() {
		return BasicType.INT.getGeneratedName();
	}
	
	@Override
	public String getStructName() {
		return structName;
	}

	@Override
	public void setName(String name) {
		structName = name;
	}
	
	

}
