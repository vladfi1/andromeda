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

public class UnaryExpression extends Expression {

  private Expression expression;
  private int operator;

  public UnaryExpression (Expression expression, int operator) {
    this.expression = expression;
    if (expression != null) expression.setParent(this);
    this.operator = operator;
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  public int getOperator() {
    return operator;
  }

  public void setOperator(int operator) {
    this.operator = operator;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (expression != null) expression.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (expression != null) expression.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (expression != null) expression.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("UnaryExpression(\n");
      if (expression != null)
        buffer.append(expression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+operator);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [UnaryExpression]");
    return buffer.toString();
  }
}
