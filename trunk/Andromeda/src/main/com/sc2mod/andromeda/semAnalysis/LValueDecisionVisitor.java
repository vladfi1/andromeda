package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VisitorAdapter;

/**
 * This visitor decides for an expression, if it is valid to be used
 * as LValue (the value at the left side of an assignment).
 * 
 * Examples:
 * x.y is a correct lValue because x.y = ... is valid.
 * (a + b) is no correct lValue because (a + b) = ... is invalid.
 * 
 * @author gex
 *
 */
public class LValueDecisionVisitor extends VisitorAdapter<Void, Boolean> {
	
	public boolean isExprValidLValue(ExprNode expr){
		return expr.accept(this,null);
	}
	
	@Override
	public Boolean visitDefault(SyntaxNode s, Void state) {
		//by default, nothing is a valid lValue
		return false;
	}
	
	//Fields, names and array accesses are valid lValues
	
	@Override
	public Boolean visit(FieldAccessExprNode fieldAccessExprNode, Void state) {
		return true;
	}
	
	@Override
	public Boolean visit(NameExprNode nameExprNode, Void state) {
		return true;
	}
	
	@Override
	public Boolean visit(ArrayAccessExprNode arrayAccessExprNode, Void state) {
		return true;
	}

}
