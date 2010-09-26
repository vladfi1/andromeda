/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codetransform;

import com.sc2mod.andromeda.syntaxNodes.AccessTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignOpTypeSE;
import com.sc2mod.andromeda.syntaxNodes.AssignmentTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.VarDecl;

public class ComplexExpressionChopper {
	
	private SyntaxGenerator syntaxGenerator;
	private ImplicitLocalVarProvider varProvider;
	
	
	public ComplexExpressionChopper(SyntaxGenerator syntaxGenerator2,
			ImplicitLocalVarProvider varProvider2) {
		this.syntaxGenerator = syntaxGenerator2;
		this.varProvider = varProvider2;
	}

	/**
	 * Returns a "reusable" presentation of an lValue
	 * expression.
	 * This means it can be used more than once without creating side effects.
	 * However, some statements might be prepended to the expression to make it work
	 * @param toChop
	 * @param addStatementsTo 
	 * @return
	 */
	public ExprNode chop(ExprNode toChop, TransformationVisitor addStatementsTo){
		
		if(toChop instanceof ArrayAccessExprNode){
			ArrayAccessExprNode a = (ArrayAccessExprNode) toChop;
			ExprNode arrayIndex = a.getRightExpression();
			if(!arrayIndex.getSimple()){
				//The index is not simple we need a local variable for it
				FieldAccessExprNode z = varProvider.getImplicitLocalVar(BasicType.INT);					
				addStatementsTo.addStatementBefore(syntaxGenerator.genAssignStatement(z, arrayIndex, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
				arrayIndex = z;				
			}
			
			//Now the prefix!
			ExprNode prefix = chop(a.getPrefix(), addStatementsTo);
			return syntaxGenerator.genArrayAccess(prefix, arrayIndex, toChop.getInferedType(), (VarDecl) a.getSemantics());
			
		} else if(toChop instanceof FieldAccessExprNode){
			FieldAccessExprNode f = (FieldAccessExprNode) toChop;
			int accessType = f.getAccessType();
			switch(accessType){
			case AccessTypeSE.EXPRESSION:
				ExprNode e = f.getLeftExpression();
				Type t = e.getInferedType();
				if(t.isValidAsParameter()){
					FieldAccessExprNode z = varProvider.getImplicitLocalVar(t);					
					addStatementsTo.addStatementBefore(syntaxGenerator.genAssignStatement(z, e, AssignOpTypeSE.EQ, AssignmentTypeSE.FIELD));
					return syntaxGenerator.genFieldAccess(z, AccessTypeSE.EXPRESSION, f.getName(), f.getInferedType(), (VarDecl) f.getSemantics());
					
				} else {
					//No valid parameter, we have to chop further :(
					ExprNode chopped = chop(e,addStatementsTo);
					return syntaxGenerator.genFieldAccess(chopped, AccessTypeSE.EXPRESSION, f.getName(), f.getInferedType(), (VarDecl) e.getSemantics());
					
				}
				
			default:
				//Everything that is no expression field access doesn't have to be chopped
				return toChop;
			}
		} else {
			throw new Error("Unsupported chop class" + toChop.getClass().getSimpleName());
		}
	}
}
