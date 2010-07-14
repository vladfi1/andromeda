/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.syntaxNodes;

public abstract class Statement extends SyntaxNode {

  private Statement successor;
  private com.sc2mod.andromeda.semAnalysis.SuccessorList successors;
  private SyntaxNode parent;

  public StatementList getStatements() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setStatements(StatementList statements) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getExpression() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setExpression(Expression expression) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ClassDeclaration getClassDeclaration() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setClassDeclaration(ClassDeclaration classDeclaration) {
    throw new ClassCastException("tried to call abstract method");
  }

  public LocalVariableDeclaration getVarDeclaration() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setVarDeclaration(LocalVariableDeclaration varDeclaration) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getCondition() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setCondition(Expression condition) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Statement getThenStatement() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setThenStatement(Statement thenStatement) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Statement getElseStatement() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setElseStatement(Statement elseStatement) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Statement getForInit() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setForInit(Statement forInit) {
    throw new ClassCastException("tried to call abstract method");
  }

  public BlockStatement getForUpdate() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setForUpdate(BlockStatement forUpdate) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Type getIteratorType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setIteratorType(Type iteratorType) {
    throw new ClassCastException("tried to call abstract method");
  }

  public VariableDecl getIterator() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setIterator(VariableDecl iterator) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getFromExpr() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setFromExpr(Expression fromExpr) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getToExpr() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setToExpr(Expression toExpr) {
    throw new ClassCastException("tried to call abstract method");
  }

  public String getLabel() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setLabel(String label) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getResult() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setResult(Expression result) {
    throw new ClassCastException("tried to call abstract method");
  }

  public boolean getUseSuper() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setUseSuper(boolean useSuper) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ExpressionList getArguments() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setArguments(ExpressionList arguments) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Statement getSuccessor() {
    return successor;
  }

  public void setSuccessor(Statement successor) {
    this.successor = successor;
  }

  public com.sc2mod.andromeda.semAnalysis.SuccessorList getSuccessors() {
    return successors;
  }

  public void setSuccessors(com.sc2mod.andromeda.semAnalysis.SuccessorList successors) {
    this.successors = successors;
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public abstract void accept(Visitor visitor);
  public abstract void childrenAccept(Visitor visitor);
  public abstract void traverseTopDown(Visitor visitor);
  public abstract void traverseBottomUp(Visitor visitor);
  public String toString() {
    return toString("");
  }

  public abstract String toString(String tab);
}
