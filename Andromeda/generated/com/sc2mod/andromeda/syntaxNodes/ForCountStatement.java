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

public class ForCountStatement extends Statement {

  private Type iteratorType;
  private VariableDecl iterator;
  private Expression fromExpr;
  private Expression toExpr;
  private Statement thenStatement;

  public ForCountStatement (Type iteratorType, VariableDecl iterator, Expression fromExpr, Expression toExpr, Statement thenStatement) {
    this.iteratorType = iteratorType;
    if (iteratorType != null) iteratorType.setParent(this);
    this.iterator = iterator;
    if (iterator != null) iterator.setParent(this);
    this.fromExpr = fromExpr;
    if (fromExpr != null) fromExpr.setParent(this);
    this.toExpr = toExpr;
    if (toExpr != null) toExpr.setParent(this);
    this.thenStatement = thenStatement;
    if (thenStatement != null) thenStatement.setParent(this);
  }

  public Type getIteratorType() {
    return iteratorType;
  }

  public void setIteratorType(Type iteratorType) {
    this.iteratorType = iteratorType;
  }

  public VariableDecl getIterator() {
    return iterator;
  }

  public void setIterator(VariableDecl iterator) {
    this.iterator = iterator;
  }

  public Expression getFromExpr() {
    return fromExpr;
  }

  public void setFromExpr(Expression fromExpr) {
    this.fromExpr = fromExpr;
  }

  public Expression getToExpr() {
    return toExpr;
  }

  public void setToExpr(Expression toExpr) {
    this.toExpr = toExpr;
  }

  public Statement getThenStatement() {
    return thenStatement;
  }

  public void setThenStatement(Statement thenStatement) {
    this.thenStatement = thenStatement;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (iteratorType != null) iteratorType.accept(visitor);
    if (iterator != null) iterator.accept(visitor);
    if (fromExpr != null) fromExpr.accept(visitor);
    if (toExpr != null) toExpr.accept(visitor);
    if (thenStatement != null) thenStatement.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (iteratorType != null) iteratorType.traverseTopDown(visitor);
    if (iterator != null) iterator.traverseTopDown(visitor);
    if (fromExpr != null) fromExpr.traverseTopDown(visitor);
    if (toExpr != null) toExpr.traverseTopDown(visitor);
    if (thenStatement != null) thenStatement.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (iteratorType != null) iteratorType.traverseBottomUp(visitor);
    if (iterator != null) iterator.traverseBottomUp(visitor);
    if (fromExpr != null) fromExpr.traverseBottomUp(visitor);
    if (toExpr != null) toExpr.traverseBottomUp(visitor);
    if (thenStatement != null) thenStatement.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ForCountStatement(\n");
      if (iteratorType != null)
        buffer.append(iteratorType.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (iterator != null)
        buffer.append(iterator.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (fromExpr != null)
        buffer.append(fromExpr.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (toExpr != null)
        buffer.append(toExpr.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (thenStatement != null)
        buffer.append(thenStatement.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ForCountStatement]");
    return buffer.toString();
  }
}
