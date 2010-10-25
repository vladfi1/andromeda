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
import com.sc2mod.andromeda.syntaxNodes.AccessTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;

/**
 * Decides if a field access is simple
 * @author us0r
 *
 */
public class SimplicityDecider {
	
	public static boolean isSimple(FieldAccessExprNode field){
		VarDecl vd = (VarDecl) field.getSemantics();
		//Accessors are not simple!
		if(vd.isAccessor()){
			return false;
		}
		int accessType = field.getAccessType();
		switch(accessType){
		case AccessTypeSE.NAMED_SUPER:
		case AccessTypeSE.SUPER:
		case AccessTypeSE.SIMPLE:
			return true;
		case AccessTypeSE.POINTER:
		case AccessTypeSE.EXPRESSION:
			//Expression left = field.getLeftExpression();
			return vd.isStatic();
		default:
			throw new Error("Unkown field access type!");
		}
	}
}
