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

public class ForEachStatement extends Statement {

  private Type iteratorType;
  private VariableDecl iterator;
  private Expression expression;
  private Statement thenStatement;

  public ForEachStatement (Type iteratorType, VariableDecl iterator, Expression expression, Statement thenStatement) {
    this.iteratorType = iteratorType;
    if (iteratorType != null) iteratorType.setParent(this);
    this.iterator = iterator;
    if (iterator != null) iterator.setParent(this);
    this.expression = expression;
    if (expression != null) expression.setParent(this);
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

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
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
    if (expression != null) expression.accept(visitor);
    if (thenStatement != null) thenStatement.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (iteratorType != null) iteratorType.traverseTopDown(visitor);
    if (iterator != null) iterator.traverseTopDown(visitor);
    if (expression != null) expression.traverseTopDown(visitor);
    if (thenStatement != null) thenStatement.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (iteratorType != null) iteratorType.traverseBottomUp(visitor);
    if (iterator != null) iterator.traverseBottomUp(visitor);
    if (expression != null) expression.traverseBottomUp(visitor);
    if (thenStatement != null) thenStatement.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ForEachStatement(\n");
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
      if (expression != null)
        buffer.append(expression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (thenStatement != null)
        buffer.append(thenStatement.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ForEachStatement]");
    return buffer.toString();
  }
}
