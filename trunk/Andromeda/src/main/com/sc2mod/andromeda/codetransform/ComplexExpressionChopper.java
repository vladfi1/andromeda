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

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.semAnalysis.SimplicityDecisionVisitor;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;

public class ComplexExpressionChopper {
	
	private SyntaxGenerator syntaxGenerator;
	private ImplicitLocalVarProvider varProvider;
	private SimplicityDecisionVisitor simpleDecider = new SimplicityDecisionVisitor();
	
	
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
			if(!simpleDecider.isSimple(arrayIndex)){
				//The index is not simple we need a local variable for it
				NameExprNode z = varProvider.getImplicitLocalVar(BasicType.INT);					
				addStatementsTo.addStatementBefore(syntaxGenerator.genAssignStatement(z, arrayIndex, AssignOpSE.EQ));
				arrayIndex = z;				
			}
			
			//Now the prefix!
			ExprNode prefix = chop(a.getPrefix(), addStatementsTo);
			return syntaxGenerator.genArrayAccess(prefix, arrayIndex, toChop.getInferedType(), (VarDecl) a.getSemantics());
			
		} else if(toChop instanceof FieldAccessExprNode){
			FieldAccessExprNode f = (FieldAccessExprNode) toChop;
			VarDecl vd = f.getSemantics();
			
			//No need to chop static variables
			if(vd.isStatic()){
				return toChop;
			}
			
			
			ExprNode e = f.getLeftExpression();
			IType t = e.getInferedType();
			if(t.isValidAsParameter()){
				NameExprNode z = varProvider.getImplicitLocalVar(t);					
				addStatementsTo.addStatementBefore(syntaxGenerator.genAssignStatement(z, e, AssignOpSE.EQ));
				return syntaxGenerator.genFieldAccess(z, f.getName(), f.getInferedType(), (VarDecl) f.getSemantics());
				
			} else {
				//No valid parameter, we have to chop further :(
				ExprNode chopped = chop(e,addStatementsTo);
				return syntaxGenerator.genFieldAccess(chopped, f.getName(), f.getInferedType(), (VarDecl) e.getSemantics());
				
			}
		
			
		} else if(toChop instanceof NameExprNode){
			//simple names do not have to be chopped
			return toChop;
		} else {
			throw new InternalProgramError(toChop,"Unsupported chop class" + toChop.getClass().getSimpleName());
		}
	}
}
