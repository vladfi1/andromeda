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

public class FieldAccess extends Expression {

  private Expression leftExpression;
  private int accessType;
  private String name;

  public FieldAccess (Expression leftExpression, int accessType, String name) {
    this.leftExpression = leftExpression;
    if (leftExpression != null) leftExpression.setParent(this);
    this.accessType = accessType;
    this.name = name;
  }

  public Expression getLeftExpression() {
    return leftExpression;
  }

  public void setLeftExpression(Expression leftExpression) {
    this.leftExpression = leftExpression;
  }

  public int getAccessType() {
    return accessType;
  }

  public void setAccessType(int accessType) {
    this.accessType = accessType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (leftExpression != null) leftExpression.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (leftExpression != null) leftExpression.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (leftExpression != null) leftExpression.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("FieldAccess(\n");
      if (leftExpression != null)
        buffer.append(leftExpression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+accessType);
    buffer.append("\n");
    buffer.append("  "+tab+name);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [FieldAccess]");
    return buffer.toString();
  }
}
