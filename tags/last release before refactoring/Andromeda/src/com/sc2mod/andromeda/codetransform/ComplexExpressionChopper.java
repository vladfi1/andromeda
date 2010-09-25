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

import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccess;
import com.sc2mod.andromeda.syntaxNodes.AssignmentOperatorType;
import com.sc2mod.andromeda.syntaxNodes.AssignmentType;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.StatementList;
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
	public Expression chop(Expression toChop, TransformationVisitor addStatementsTo){
		
		if(toChop instanceof ArrayAccess){
			ArrayAccess a = (ArrayAccess) toChop;
			Expression arrayIndex = a.getRightExpression();
			if(!arrayIndex.getSimple()){
				//The index is not simple we need a local variable for it
				FieldAccess z = varProvider.getImplicitLocalVar(BasicType.INT);					
				addStatementsTo.addStatementBefore(syntaxGenerator.genAssignStatement(z, arrayIndex, AssignmentOperatorType.EQ, AssignmentType.FIELD));
				arrayIndex = z;				
			}
			
			//Now the prefix!
			Expression prefix = chop(a.getPrefix(), addStatementsTo);
			return syntaxGenerator.genArrayAccess(prefix, arrayIndex, toChop.getInferedType(), (VarDecl) a.getSemantics());
			
		} else if(toChop instanceof FieldAccess){
			FieldAccess f = (FieldAccess) toChop;
			int accessType = f.getAccessType();
			switch(accessType){
			case AccessType.EXPRESSION:
				Expression e = f.getLeftExpression();
				Type t = e.getInferedType();
				if(t.isValidAsParameter()){
					FieldAccess z = varProvider.getImplicitLocalVar(t);					
					addStatementsTo.addStatementBefore(syntaxGenerator.genAssignStatement(z, e, AssignmentOperatorType.EQ, AssignmentType.FIELD));
					return syntaxGenerator.genFieldAccess(z, AccessType.EXPRESSION, f.getName(), f.getInferedType(), (VarDecl) f.getSemantics());
					
				} else {
					//No valid parameter, we have to chop further :(
					Expression chopped = chop(e,addStatementsTo);
					return syntaxGenerator.genFieldAccess(chopped, AccessType.EXPRESSION, f.getName(), f.getInferedType(), (VarDecl) e.getSemantics());
					
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
