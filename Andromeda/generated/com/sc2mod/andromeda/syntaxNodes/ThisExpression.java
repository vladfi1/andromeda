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

public class ThisExpression extends Expression {

  private Expression thisClassName;

  public ThisExpression (Expression thisClassName) {
    this.thisClassName = thisClassName;
    if (thisClassName != null) thisClassName.setParent(this);
  }

  public Expression getThisClassName() {
    return thisClassName;
  }

  public void setThisClassName(Expression thisClassName) {
    this.thisClassName = thisClassName;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (thisClassName != null) thisClassName.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (thisClassName != null) thisClassName.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (thisClassName != null) thisClassName.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ThisExpression(\n");
      if (thisClassName != null)
        buffer.append(thisClassName.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ThisExpression]");
    return buffer.toString();
  }
}