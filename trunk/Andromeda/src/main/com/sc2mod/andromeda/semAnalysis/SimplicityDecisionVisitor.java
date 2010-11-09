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


import com.sc2mod.andromeda.environment.access.AccessType;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.SuperExprNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExprNode;
import com.sc2mod.andromeda.syntaxNodes.util.VisitorAdapter;

/**
 * Decides if an expression is to be considered simple.
 * 
 * A simple expression may not have side effects and should be considered not to be computation intensive.
 * 
 * The idea behind this is that simple expressions can be duplicated when optimizing code,
 * for example 
 * 		z = (x.y += 2);
 * can be transformed to 
 * 		x.y = x.y + 2;
 * 		z = x.y;
 * only if x.y is considered simple. Otherwise the value of x.y will be cached so that it is not read twice
 * (which could have side effects or need too much computation time). The code would then look like this:
 * 		int impl = x.y + 2; //impl is an implicitly generated variable
 * 		x.y = impl;
 * 		y = impl;
 * 
 * @author gex
 *
 */
public class SimplicityDecisionVisitor extends VisitorAdapter<Void, Boolean> {
	
	public boolean isSimple(ExprNode s){
		return s.accept(this,null);
	}
	
	@Override
	public Boolean visitDefault(SyntaxNode s, Void state) {
		//by default, nothing is simple.
		return false;
	}
	
	@Override
	public Boolean visit(LiteralExprNode literalExprNode, Void state) {
		return true;
	}
	
	@Override
	public Boolean visit(NameExprNode nameExprNode, Void state) {
		NameAccess vd = nameExprNode.getSemantics();
		//Accessors are not simple!
		if(vd.getAccessType() == AccessType.ACCESSOR){
			return false;
		}
		
		//otherwise it is simple
		return true;
	}
	
	
	@Override
	public Boolean visit(FieldAccessExprNode fieldAccessExprNode, Void state) {
		NameAccess vd = fieldAccessExprNode.getSemantics();
		//Accessors are not simple!
		if(vd.getAccessType() == AccessType.ACCESSOR){
			return false;
		}
		
		//Right now, only static accesses are simple
		return vd.isStatic();
		
	}
	
	@Override
	public Boolean visit(ThisExprNode thisExprNode, Void state) {
		return true;
	}
	
	@Override
	public Boolean visit(SuperExprNode superExprNode, Void state) {
		return true;
	}

}
