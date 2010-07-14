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

public class Assignment extends Expression {

  private int assignmentType;
  private Expression leftExpression;
  private int operator;
  private Expression rightExpression;

  public Assignment (int assignmentType, Expression leftExpression, int operator, Expression rightExpression) {
    this.assignmentType = assignmentType;
    this.leftExpression = leftExpression;
    if (leftExpression != null) leftExpression.setParent(this);
    this.operator = operator;
    this.rightExpression = rightExpression;
    if (rightExpression != null) rightExpression.setParent(this);
  }

  public int getAssignmentType() {
    return assignmentType;
  }

  public void setAssignmentType(int assignmentType) {
    this.assignmentType = assignmentType;
  }

  public Expression getLeftExpression() {
    return leftExpression;
  }

  public void setLeftExpression(Expression leftExpression) {
    this.leftExpression = leftExpression;
  }

  public int getOperator() {
    return operator;
  }

  public void setOperator(int operator) {
    this.operator = operator;
  }

  public Expression getRightExpression() {
    return rightExpression;
  }

  public void setRightExpression(Expression rightExpression) {
    this.rightExpression = rightExpression;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (leftExpression != null) leftExpression.accept(visitor);
    if (rightExpression != null) rightExpression.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (leftExpression != null) leftExpression.traverseTopDown(visitor);
    if (rightExpression != null) rightExpression.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (leftExpression != null) leftExpression.traverseBottomUp(visitor);
    if (rightExpression != null) rightExpression.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("Assignment(\n");
    buffer.append("  "+tab+assignmentType);
    buffer.append("\n");
      if (leftExpression != null)
        buffer.append(leftExpression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+operator);
    buffer.append("\n");
      if (rightExpression != null)
        buffer.append(rightExpression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [Assignment]");
    return buffer.toString();
  }
}
