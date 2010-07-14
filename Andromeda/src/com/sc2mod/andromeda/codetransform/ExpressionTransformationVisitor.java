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

import com.sc2mod.andromeda.parsing.VisitorErrorAdapater;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccess;
import com.sc2mod.andromeda.syntaxNodes.Assignment;
import com.sc2mod.andromeda.syntaxNodes.AssignmentOperatorType;
import com.sc2mod.andromeda.syntaxNodes.BinaryExpression;
import com.sc2mod.andromeda.syntaxNodes.CastExpression;
import com.sc2mod.andromeda.syntaxNodes.ClassInstanceCreationExpression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocation;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExpression;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExpression;
import com.sc2mod.andromeda.syntaxNodes.UnaryExpression;

public class ExpressionTransformationVisitor extends VisitorErrorAdapater {

	
	protected TransformationVisitor parent;
	protected SyntaxGenerator syntaxGenerator;
	boolean isInsideExpression;
	boolean isWrite;
	boolean isRead = true;
	protected Options options;
	
	public ExpressionTransformationVisitor(Options options) {
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
	public void visit(LiteralExpression literalExpression) {}
	@Override
	public void visit(ThisExpression thisExpression) {}
	@Override
	public void visit(KeyOfExpression keyOfExpression) {}

	@Override
	public void visit(FieldAccess fieldAccess) {
		
		//Do left expresion
		invokeSelf(fieldAccess.getLeftExpression());
		if (parent.replaceExpression != null) {
			fieldAccess.setLeftExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	@Override
	public void visit(ArrayAccess arrayAccess) {
		//Do left expresion
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
	public void visit(MethodInvocation methodInvocation) {
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
	public void visit(CastExpression castExpression) {
		//Expression
		invokeSelf(castExpression.getRightExpression());
		if(parent.replaceExpression != null){
			castExpression.setRightExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	@Override
	public void visit(ExpressionList e) {
		int size = e.size();
		for(int i=0;i<size;i++){
			invokeSelf(e.elementAt(i));
			if (parent.replaceExpression != null) {
				e.setElementAt(parent.replaceExpression,i);
				parent.replaceExpression = null;
			}
		}
	}
	
	
	@Override
	public void visit(BinaryExpression a) {
		
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
	public void visit(ParenthesisExpression a) {
		
		invokeSelf(a.getExpression());
		if(parent.replaceExpression!=null){
			a.setExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	}
	
	
	@Override
	public void visit(UnaryExpression unaryExpression) {
		
		invokeSelf(unaryExpression.getExpression());
		if(parent.replaceExpression!=null){
			unaryExpression.setExpression(parent.replaceExpression);
			parent.replaceExpression = null;
		}
	
	}
	
	@Override
	public void visit(ClassInstanceCreationExpression c) {
		//Visit arguments
		invokeSelf(c.getArguments());
	}
	
	
	@Override
	public void visit(Assignment a) {
		invokeSelfLValue(a.getLeftExpression(),a.getOperator()!=AssignmentOperatorType.EQ);
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
