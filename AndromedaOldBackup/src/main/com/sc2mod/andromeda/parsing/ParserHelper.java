/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AccessTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.QualifiedTypeNode;
import com.sc2mod.andromeda.syntaxNodes.SimpleTypeNode;
import com.sc2mod.andromeda.syntaxNodes.TypeNode;
import com.sc2mod.andromeda.syntaxNodes.TypeCategorySE;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;

public class ParserHelper {

	public static ArrayAccessExprNode arrayTypeToAccess(ArrayTypeNode a) {
		
		ExprListNode dimensions = a.getDimensions();
		int size = dimensions.size();
		TypeNode wrappedType = a.getWrappedType();
//		if(wrappedType.getCategory()!=TypeCategory.SIMPLE)
//			throw new CompilationError(a, "Invalid array access.");
		ExprNode e = null;
		switch(wrappedType.getCategory()){
		case TypeCategorySE.SIMPLE:
			e = new FieldAccessExprNode(null,AccessTypeSE.SIMPLE,wrappedType.getName());
			break;
		case TypeCategorySE.ARRAY:
			throw new InternalProgramError("Array in array?");
			//break;
		case TypeCategorySE.QUALIFIED:
			e = wrappedType.getQualifiedName();
			break;
		case TypeCategorySE.BASIC:
			throw new InternalProgramError("Basic type as an expression?");
			//break;
		default:
			throw new InternalProgramError("unsupported type!");
		}
		e.setPos(wrappedType.getLeftPos(), wrappedType.getRightPos());
		for(int i=0;i<size;i++){
			ExprNode dim = dimensions.elementAt(i);
			int right = dim.getRightPos();
			int left = e.getLeftPos();
			e = new ArrayAccessExprNode(e, dim);
			e.setPos(left, right);
		}
		return (ArrayAccessExprNode) e;
	}
	
	
	public static TypeNode getExpressionType(ExprNode e, TypeListNode typeArguments){
		TypeNode t;
		if(e instanceof FieldAccessExprNode){
			if(e.getLeftExpression()==null){
				t = new SimpleTypeNode(TypeCategorySE.SIMPLE, e.getName(),typeArguments);
			} else {
				t = new QualifiedTypeNode(TypeCategorySE.QUALIFIED, (FieldAccessExprNode) e,typeArguments);
			}
		} else {
			throw new InternalProgramError("non type expession. please bug report immediately!");
		}
		t.setPos(e.getLeftPos(), e.getRightPos());
		return t;
	}


	public static ExprNode createInlineMethodInvocation(ExprNode p,
			ExprListNode a) {
		throw new InternalProgramError("Inline method calls not implemented yet!");
	}

}
