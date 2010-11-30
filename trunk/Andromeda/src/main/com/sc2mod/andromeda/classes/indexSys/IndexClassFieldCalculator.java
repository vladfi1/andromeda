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

import java.util.ArrayList;

import com.sc2mod.andromeda.classes.ClassFieldCalculator;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.IndexInformation;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.variables.Variable;

/**
 * Calculates which field a class hierarchy needs.
 * 
 * This is needed at the moment, since a class hierarchy is compiled to
 * only ONE struct which should have as few fields as possible to save space.
 * 
 * Should get obsolete if we find a way to cast easily between structs so each
 * class can become its own struct.
 * 
 * @author J. 'gex' Finis
 *
 */
public class IndexClassFieldCalculator extends ClassFieldCalculator {

	public IndexClassFieldCalculator(Environment tp, INameProvider snv, IndexInformation indexInfo) {
		super(tp,snv, indexInfo);
	}

	@Override
	protected ArrayList<Variable> generateImplicitFields(IClass c) {
		ArrayList<Variable> result = new ArrayList<Variable>();
		if(c.isTopType()){
			BasicType INT = typeProvider.BASIC.INT;
			result.add(createField(c,INT,"__id"));
			result.add(createField(c,INT,"__type"));
		} 
		return result;
	}

	@Override
	public void generateClassNames() {	
//		for(Class c: typeProvider.getClasses()){
//			//if(!c.isTopClass()){
//				c.setGeneratedName(c.getTopClass().getGeneratedDefinitionName());
//			//}
//		}
	}


}
