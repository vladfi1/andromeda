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

public class ConditionalExpression extends Expression {

  private Expression leftExpression;
  private Expression rightExpression;
  private Expression rightExpression2;

  public ConditionalExpression (Expression leftExpression, Expression rightExpression, Expression rightExpression2) {
    this.leftExpression = leftExpression;
    if (leftExpression != null) leftExpression.setParent(this);
    this.rightExpression = rightExpression;
    if (rightExpression != null) rightExpression.setParent(this);
    this.rightExpression2 = rightExpression2;
    if (rightExpression2 != null) rightExpression2.setParent(this);
  }

  public Expression getLeftExpression() {
    return leftExpression;
  }

  public void setLeftExpression(Expression leftExpression) {
    this.leftExpression = leftExpression;
  }

  public Expression getRightExpression() {
    return rightExpression;
  }

  public void setRightExpression(Expression rightExpression) {
    this.rightExpression = rightExpression;
  }

  public Expression getRightExpression2() {
    return rightExpression2;
  }

  public void setRightExpression2(Expression rightExpression2) {
    this.rightExpression2 = rightExpression2;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (leftExpression != null) leftExpression.accept(visitor);
    if (rightExpression != null) rightExpression.accept(visitor);
    if (rightExpression2 != null) rightExpression2.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (leftExpression != null) leftExpression.traverseTopDown(visitor);
    if (rightExpression != null) rightExpression.traverseTopDown(visitor);
    if (rightExpression2 != null) rightExpression2.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (leftExpression != null) leftExpression.traverseBottomUp(visitor);
    if (rightExpression != null) rightExpression.traverseBottomUp(visitor);
    if (rightExpression2 != null) rightExpression2.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ConditionalExpression(\n");
      if (leftExpression != null)
        buffer.append(leftExpression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (rightExpression != null)
        buffer.append(rightExpression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (rightExpression2 != null)
        buffer.append(rightExpression2.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ConditionalExpression]");
    return buffer.toString();
  }
}
