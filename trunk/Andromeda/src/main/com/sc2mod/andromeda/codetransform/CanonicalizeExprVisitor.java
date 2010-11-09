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

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.AccessType;
import com.sc2mod.andromeda.environment.access.AccessorAccess;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.access.VarAccess;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.UsageType;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.parsing.CompilerThread;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public class CanonicalizeExprVisitor extends TransformationExprVisitor {

	private UglyExprTransformer exprTransformer;
	private TypeProvider typeProvider;
	private boolean resolveConst;
	
	public CanonicalizeExprVisitor(Configuration options, TypeProvider typeProvider) {
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
		if(val == null){
			throw new InternalProgramError(e, "Constant but no value?");
		}
		if(val==null)
			throw new Error(e.toString());
		if(val.doNotInline()) return false;
		
		parent.replaceExpression = val.getExpression();
		if(isFieldAccess){
			VarAccess vd = (VarAccess) e.getSemantics();
			vd.getAccessedElement().registerInline();
		}
		return true;
	}
	

	protected boolean replaceByConst(ExprNode e) {
		return replaceByConst(e, false);
	}
	
	private void replaceAccessor(ExprNode nameExpr, ExprNode prefix) {
		NameAccess na = (NameAccess) nameExpr.getSemantics();
		if(na.getAccessType() != AccessType.ACCESSOR)
			return;
		
		AccessorAccess v = (AccessorAccess) na;
		//only getters (rValues) are replaced, rest is replaced by the UglyTransformer
		if(v.getUsageType() != UsageType.RVALUE)
			return;
		Operation getMethod = v.getGetMethod();
		
		MethodInvocationExprNode invocationNode = new MethodInvocationExprNode(prefix, getMethod.getUid(), new ExprListNode(), null);
		Invocation inv = ResolveUtil.createInvocation(getMethod, false);
		invocationNode.setSemantics(inv);
		invocationNode.setInferedType(v.getType());
		
		//replace the access by the method
		parent.replaceExpression = invocationNode;
		
	}

	@Override
	public void visit(NameExprNode nameExprNode) {
		if(replaceByConst(nameExprNode,true)) return;	
		
		replaceAccessor(nameExprNode,null);
	}

	@Override
	public void visit(FieldAccessExprNode fieldAccess) {
		if(replaceByConst(fieldAccess,true)) return;		

		//System.out.println(fieldAccess.getName());
		
		//Do children
		super.visit(fieldAccess);
		
		//Check name
		replaceAccessor(fieldAccess,fieldAccess.getLeftExpression());
		
		
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
