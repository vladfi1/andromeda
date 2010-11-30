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

import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.NewExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.util.visitors.VoidVisitorErrorAdapater;

public class TransformationExprVisitor extends VoidVisitorErrorAdapater {

	
	protected TransformationVisitor parent;
	protected SyntaxGenerator syntaxGenerator;
	boolean isInsideExpression;
	boolean isWrite;
	boolean isRead = true;
	protected Configuration options;
	
	public TransformationExprVisitor(Configuration options) {
		this.options = options;
	}
	
	public void setParent(TransformationVisitor t){
		parent = t;
		this.syntaxGenerator = parent.syntaxGenerator;
	}
	
	protected void invokeSelf(SyntaxNode toInvoke){
		if(toInvoke==null) return;
		boolean insideBefore = isInsideExpression;
		boolean writeBefore = isWrite;
		boolean readBefore = isRead;
		isWrite = false;
		isRead = true;
		isInsideExpression = true;
		toInvoke.accept(this);
		isInsideExpression = insideBefore;
		isWrite = writeBefore;
		isRead = readBefore;
	}
	
	protected void invokeSelfLValue(SyntaxNode toInvoke, boolean read){
		boolean insideBefore = isInsideExpression;
		boolean writeBefore = isWrite;
		boolean readBefore = isRead;
		isWrite = true;
		isRead = read;
		isInsideExpression = true;
		toInvoke.accept(this);
		isInsideExpression = insideBefore;
		isWrite = writeBefore;
		isRead = readBefore;
	}
	
	@Override
	public void visit(LiteralExprNode literalExpression) {}
	@Override
	public void visit(ThisExprNode thisExpression) {}

	@Override
	public void visit(FieldAccessExprNode fieldAccess) {
		
		//Do left expression
		invokeSelf(fieldAccess.getLeftExpression());
		if (parent.replaceExpression != null) {
			fieldAccess.setLeftExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	@Override
	public void visit(NameExprNode nameExprNode) {	}
	
	@Override
	public void visit(ArrayAccessExprNode arrayAccess) {
		//Do left expression
		invokeSelf(arrayAccess.getLeftExpression());
		if (parent.replaceExpression != null) {
			arrayAccess.setLeftExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
		
		//Do right expression
		invokeSelf(arrayAccess.getRightExpression());
		if (parent.replaceExpression != null) {
			arrayAccess.setRightExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	@Override
	public void visit(MethodInvocationExprNode methodInvocation) {
		//Prefix
		invokeSelf(methodInvocation.getPrefix());
		if (parent.replaceExpression != null) {
			methodInvocation.setPrefix(parent.replaceExpression);
			parent.replaceExpression = null;
		} 
		
		//Expression list
		invokeSelf(methodInvocation.getArguments());
	}
	
	@Override
	public void visit(CastExprNode castExpression) {
		//Expression
		invokeSelf(castExpression.getRightExpression());
		if(parent.replaceExpression != null){
			castExpression.setRightExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	@Override
	public void visit(ExprListNode e) {
		int size = e.size();
		for(int i=0;i<size;i++){
			invokeSelf(e.get(i));
			if (parent.replaceExpression != null) {
				e.set(i,parent.replaceExpression);
				parent.replaceExpression = null;
			}
		}
	}
	
	
	@Override
	public void visit(BinOpExprNode a) {
		
		invokeSelf(a.getLeftExpression());
		if(parent.replaceExpression!=null){
			a.setLeftExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
		invokeSelf(a.getRightExpression());
		if(parent.replaceExpression!=null){
			a.setRightExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	@Override
	public void visit(ParenthesisExprNode a) {
		
		invokeSelf(a.getExpression());
		if(parent.replaceExpression!=null){
			a.setExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	
	@Override
	public void visit(UnOpExprNode unaryExpression) {
		
		invokeSelf(unaryExpression.getExpression());
		if(parent.replaceExpression!=null){
			unaryExpression.setExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	
	}
	
	@Override
	public void visit(NewExprNode c) {
		//Visit arguments
		invokeSelf(c.getArguments());
	}
	
	@Override
	public void visit(AssignmentExprNode a) {
		invokeSelfLValue(a.getLeftExpression(),a.getAssignOp()!=AssignOpSE.EQ);
		if(parent.replaceExpression!=null){
			a.setLeftExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
		invokeSelf(a.getRightExpression());
		if(parent.replaceExpression!=null){
			a.setRightExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	
	}

}
