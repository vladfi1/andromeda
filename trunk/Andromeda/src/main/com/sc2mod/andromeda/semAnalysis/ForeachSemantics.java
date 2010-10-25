/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.operations.Invocation;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;

public class ForeachSemantics extends LoopSemantics {

	private Type nextType;
	private Type iteratorType;
	private Type itereeType;
	private Type iterVarType;
	private LocalVarDecl iterVarDecl;
	private NameExprNode iterator;

	public void setIterator(NameExprNode iterator) {
		this.iterator = iterator;
	}

	public NameExprNode getIterator() {
		return iterator;
	}
	
	public LocalVarDecl getIterVarDecl() {
		return iterVarDecl;
	}
	public void setIterVarDecl(LocalVarDecl iterVarDecl) {
		this.iterVarDecl = iterVarDecl;
	}
	private Invocation hasNext;
	private Invocation next;
	private Invocation getIterator;
	
	private StmtNode delStatement;
	
	
	public StmtNode getDelStatement() {
		return delStatement;
	}
	public void setDelStatement(StmtNode delStatemetn) {
		this.delStatement = delStatemetn;
	}
	public Type getIterVarType() {
		return iterVarType;
	}
	public void setIterVarType(Type iterVarType) {
		this.iterVarType = iterVarType;
	}
	public Invocation getGetIterator() {
		return getIterator;
	}
	public void setGetIterator(Invocation getIterator) {
		this.getIterator = getIterator;
	}
	private boolean destroyAfter;
	public Type getNextType() {
		return nextType;
	}
	public void setNextType(Type nextType) {
		this.nextType = nextType;
	}
	public Type getIteratorType() {
		return iteratorType;
	}
	public void setIteratorType(Type iteratorType) {
		this.iteratorType = iteratorType;
	}
	public Type getItereeType() {
		return itereeType;
	}
	public void setItereeType(Type itereeType) {
		this.itereeType = itereeType;
	}
	public Invocation getHasNext() {
		return hasNext;
	}
	public void setHasNext(Invocation hasNext) {
		this.hasNext = hasNext;
	}
	public Invocation getNext() {
		return next;
	}
	public void setNext(Invocation next) {
		this.next = next;
	}
	public boolean doDestroyAfter() {
		return destroyAfter;
	}
	public void setDestroyAfter(boolean destroyAfter) {
		this.destroyAfter = destroyAfter;
	}



	
	
}
