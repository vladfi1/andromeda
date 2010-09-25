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


import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;

/**
 * Decides if a field access is simple
 * @author us0r
 *
 */
public class SimplicityDecider {
	
	public static boolean isSimple(FieldAccess field){
		VarDecl vd = (VarDecl) field.getSemantics();
		//Accessors are not simple!
		if(vd.isAccessor()){
			return false;
		}
		int accessType = field.getAccessType();
		switch(accessType){
		case AccessType.NAMED_SUPER:
		case AccessType.SUPER:
		case AccessType.SIMPLE:
			return true;
		case AccessType.POINTER:
		case AccessType.EXPRESSION:
			//Expression left = field.getLeftExpression();
			return vd.isStatic();
		default:
			throw new Error("Unkown field access type!");
		}
	}
}
