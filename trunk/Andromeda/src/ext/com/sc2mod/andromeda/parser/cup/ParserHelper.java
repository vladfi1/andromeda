/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parser.cup;

import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.BasicTypeNode;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierSE;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.QualifiedTypeNode;
import com.sc2mod.andromeda.syntaxNodes.SimpleTypeNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.syntaxNodes.TypeNode;

public class ParserHelper {

	/**
	 * Used for galaxy functions. Transforms the static into the private modifier.
	 */
	public static void transformFunctionModifiers(ModifierListNode list){
		int size = list.size();
		for(int i=0;i<size;i++){
			if(list.get(i) == ModifierSE.STATIC){
				list.set(i,ModifierSE.PRIVATE);
			}
			
		}
		
	}
	
	/**
	 * This method is needed because the grammar that builds andromeda
	 * can not know from the beginning at each point if an expression of the form a[b] is an
	 * array access or a type definition (like int[10]).
	 * 
	 * Because of this, it always assumes a type definition first. If this is not correct
	 * (which is discovered later on), the built array type definition npode must be converted
	 * to an array access node. This is done by this method.
	 * @param a the array type node to be transformed into an array access
	 * @return the result of the transformation
	 */
	public static ArrayAccessExprNode arrayTypeToAccess(ArrayTypeNode a) {
		
		ExprListNode dimensions = a.getDimensions();
		TypeNode wrappedType = a.getWrappedType();
		ExprNode e = null;
		if(wrappedType instanceof SimpleTypeNode){
			e = new NameExprNode(wrappedType.getName());
			
		} else if(wrappedType instanceof QualifiedTypeNode){
			e = wrappedType.getQualifiedName();
		} else {
			throw new InternalProgramError("unsupported type for conversion from array type to array access! Please file bug report!");
		}
	
		e.setPos(wrappedType.getLeftPos(), wrappedType.getRightPos());
		for(ExprNode dim : dimensions){
			int right = dim.getRightPos();
			int left = e.getLeftPos();
			e = new ArrayAccessExprNode(e, dim);
			e.setPos(left, right);
		}
		return (ArrayAccessExprNode) e;
	}
	
	
	public static TypeNode getExpressionType(ExprNode e, TypeListNode typeArguments){
		TypeNode t;
		if(e instanceof NameExprNode){
			t = new SimpleTypeNode(e.getName(),typeArguments);
		} else
		if(e instanceof FieldAccessExprNode){
			t = new QualifiedTypeNode((FieldAccessExprNode) e,typeArguments);
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
	
	public static BlockStmtNode createBlock(StmtNode stmt){
		if(stmt instanceof BlockStmtNode){
			return (BlockStmtNode) stmt;
		}
		return (BlockStmtNode) new BlockStmtNode((StmtListNode) new StmtListNode(stmt).setPos(stmt.getLeftPos(),stmt.getRightPos())).setPos(stmt.getLeftPos(), stmt.getRightPos());
		
	}

}
