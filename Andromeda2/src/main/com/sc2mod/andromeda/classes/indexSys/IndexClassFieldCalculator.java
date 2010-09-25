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
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.syntaxNodes.ClassMemberType;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.VariableDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclaratorId;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;

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

	public IndexClassFieldCalculator(TypeProvider tp, INameProvider snv) {
		super(tp,snv);
	}

	@Override
	protected ArrayList<FieldDecl> generateImplicitFields(Class c) {
		ArrayList<FieldDecl> result = new ArrayList<FieldDecl>();
		if(c.isTopClass()){
			result.add(createField(c,BasicType.INT,"__id"));
			result.add(createField(c,BasicType.INT,"__type"));
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
