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
import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccess;
import com.sc2mod.andromeda.syntaxNodes.ArrayType;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.QualifiedType;
import com.sc2mod.andromeda.syntaxNodes.SimpleType;
import com.sc2mod.andromeda.syntaxNodes.Type;
import com.sc2mod.andromeda.syntaxNodes.TypeCategory;
import com.sc2mod.andromeda.syntaxNodes.TypeList;

public class ParserHelper {

	public static ArrayAccess arrayTypeToAccess(ArrayType a) {
		
		ExpressionList dimensions = a.getDimensions();
		int size = dimensions.size();
		Type wrappedType = a.getWrappedType();
//		if(wrappedType.getCategory()!=TypeCategory.SIMPLE)
//			throw new CompilationError(a, "Invalid array access.");
		Expression e = null;
		switch(wrappedType.getCategory()){
		case TypeCategory.SIMPLE:
			e = new FieldAccess(null,AccessType.SIMPLE,wrappedType.getName());
			break;
		case TypeCategory.ARRAY:
			throw new InternalProgramError("Array in array?");
			//break;
		case TypeCategory.QUALIFIED:
			e = wrappedType.getQualifiedName();
			break;
		case TypeCategory.BASIC:
			throw new InternalProgramError("Basic type as an expression?");
			//break;
		default:
			throw new InternalProgramError("unsupported type!");
		}
		e.setPos(wrappedType.getLeftPos(), wrappedType.getRightPos());
		for(int i=0;i<size;i++){
			Expression dim = dimensions.elementAt(i);
			int right = dim.getRightPos();
			int left = e.getLeftPos();
			e = new ArrayAccess(e, dim);
			e.setPos(left, right);
		}
		return (ArrayAccess) e;
	}
	
	
	public static Type getExpressionType(Expression e, TypeList typeArguments){
		Type t;
		if(e instanceof FieldAccess){
			if(e.getLeftExpression()==null){
				t = new SimpleType(TypeCategory.SIMPLE, e.getName(),typeArguments);
			} else {
				t = new QualifiedType(TypeCategory.QUALIFIED, (FieldAccess) e,typeArguments);
			}
		} else {
			throw new InternalProgramError("non type expession. please bug report immediately!");
		}
		t.setPos(e.getLeftPos(), e.getRightPos());
		return t;
	}


	public static Expression createInlineMethodInvocation(Expression p,
			ExpressionList a) {
		throw new InternalProgramError("Inline method calls not implemented yet!");
	}

}
