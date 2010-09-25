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
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.Assignment;
import com.sc2mod.andromeda.syntaxNodes.BinaryExpression;
import com.sc2mod.andromeda.syntaxNodes.CastExpression;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExpression;
import com.sc2mod.andromeda.syntaxNodes.UnaryExpression;
import com.sc2mod.andromeda.vm.data.DataObject;

public class CodeExpressionTransformationVisitor extends ExpressionTransformationVisitor {

	private UglyExprTransformer exprTransformer;
	private TypeProvider typeProvider;
	
	public CodeExpressionTransformationVisitor(Options options, TypeProvider typeProvider) {
		super(options);
		this.typeProvider = typeProvider;
	}
	
	@Override
	public void setParent(TransformationVisitor t) {
		// TODO Auto-generated method stub
		super.setParent(t);
		exprTransformer = new UglyExprTransformer(parent, typeProvider, parent.varProvider, syntaxGenerator);
	}
	

	protected boolean replaceByConst(Expression e, boolean isFieldAccess) {
		if(!e.getConstant()) return false;
		if(!options.resolveConstantExpressions) return false;
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
	

	protected boolean replaceByConst(Expression e) {
		return replaceByConst(e, false);
	}


	@Override
	public void visit(FieldAccess fieldAccess) {
		if(replaceByConst(fieldAccess,true)) return;		

		//System.out.println(fieldAccess.getName());
		
		//Do children
		super.visit(fieldAccess);
		
		//If this is an lvalue, do nothing
		if(isWrite){
			return;
		}
		VarDecl v = (VarDecl) fieldAccess.getSemantics();
		if(v.isAccessor()){
			parent.replaceExpression = parent.syntaxGenerator.createAccessorGet((AccessorDecl) v, fieldAccess.getAccessType(), fieldAccess.getLeftExpression(), fieldAccess.getName());
		}
	}
	
	@Override
	public void visit(BinaryExpression a) {
		if(replaceByConst(a)) return;		
		
		super.visit(a);
	}
	
	@Override
	public void visit(ParenthesisExpression a) {
		if(replaceByConst(a)) return;		
		
		super.visit(a);
	}
	
	@Override
	public void visit(CastExpression castExpression) {
		if(replaceByConst(castExpression)) return;
		
		super.visit(castExpression);
	}
	
	@Override
	public void visit(KeyOfExpression keyOfExpression) {
		parent.replaceExpression = keyOfExpression.getValue().getExpression();
	}
	
	
	@Override
	public void visit(UnaryExpression unaryExpression) {
		if(replaceByConst(unaryExpression)) return;	
		
		super.visit(unaryExpression);
		
 		parent.replaceExpression = exprTransformer.transform(unaryExpression, isInsideExpression);
	}
	
	
	@Override
	public void visit(Assignment a) {
		super.visit(a);
		parent.replaceExpression = exprTransformer.transform(a, isInsideExpression);		
	}

}
