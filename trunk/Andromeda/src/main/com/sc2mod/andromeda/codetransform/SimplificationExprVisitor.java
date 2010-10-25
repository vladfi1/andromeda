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

import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.CompilerThread;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public class SimplificationExprVisitor extends TransformationExprVisitor {

	private UglyExprTransformer exprTransformer;
	private TypeProvider typeProvider;
	private boolean resolveConst;
	
	public SimplificationExprVisitor(Configuration options, TypeProvider typeProvider) {
		super(options);
		this.resolveConst = options.getParamBool(Parameter.OPTIMIZE_RESOLVE_CONSTANT_EXPRS);
		this.typeProvider = typeProvider;
	}
	
	@Override
	public void setParent(TransformationVisitor t) {
		// TODO Auto-generated method stub
		super.setParent(t);
		exprTransformer = new UglyExprTransformer(parent, typeProvider, parent.varProvider, syntaxGenerator);
	}
	

	protected boolean replaceByConst(ExprNode e, boolean isFieldAccess) {
		if(!e.isConstant()) return false;
		if(!resolveConst) return false;
		DataObject val = e.getValue();
		if(val.doNotInline()) return false;
		if(val==null)
			throw new Error(e.toString());
		
		parent.replaceExpression = val.getExpression();
		if(isFieldAccess){
			VarDecl vd = (VarDecl) e.getSemantics();
			vd.registerInline();
		}
		return true;
	}
	

	protected boolean replaceByConst(ExprNode e) {
		return replaceByConst(e, false);
	}

	private void checkName(ExprNode nameExpr){
		//If this is an lvalue, do nothing
		if(isWrite){
			return;
		}
		VarDecl v = (VarDecl) nameExpr.getSemantics();
		
		//No semantics? Then this is a static prefix or package name. Ignore!
		if(v == null) return;
		
		if(v.isAccessor()){
			//FIXME: Accessor simplification handling
			//parent.replaceExpression = parent.syntaxGenerator.createAccessorGet((AccessorDecl) v, fieldAccess.getInvocationType(), fieldAccess.getLeftExpression(), fieldAccess.getName());
		}
	}
	
	@Override
	public void visit(NameExprNode nameExprNode) {
		if(replaceByConst(nameExprNode,true)) return;	
		
		checkName(nameExprNode);
	}

	@Override
	public void visit(FieldAccessExprNode fieldAccess) {
		if(replaceByConst(fieldAccess,true)) return;		

		//System.out.println(fieldAccess.getName());
		
		//Do children
		super.visit(fieldAccess);
		
		//Check name
		checkName(fieldAccess);
		
		
	}
	
	@Override
	public void visit(BinOpExprNode a) {
		if(replaceByConst(a)) return;		
		
		super.visit(a);
	}
	
	@Override
	public void visit(ParenthesisExprNode a) {
		if(replaceByConst(a)) return;		
		
		super.visit(a);
	}
	
	@Override
	public void visit(CastExprNode castExpression) {
		if(replaceByConst(castExpression)) return;
		
		super.visit(castExpression);
	}
	
	@Override
	public void visit(KeyOfExprNode keyOfExpression) {
		parent.replaceExpression = keyOfExpression.getValue().getExpression();
	}
	
	
	@Override
	public void visit(UnOpExprNode unaryExpression) {
		if(replaceByConst(unaryExpression)) return;	
		
		super.visit(unaryExpression);
		
 		parent.replaceExpression = exprTransformer.transform(unaryExpression, isInsideExpression);
	}
	
	
	@Override
	public void visit(AssignmentExprNode a) {
		super.visit(a);
		parent.replaceExpression = exprTransformer.transform(a, isInsideExpression);		
	}

}
