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

public class SuperExpression extends Expression {

  private Expression superClassName;

  public SuperExpression (Expression superClassName) {
    this.superClassName = superClassName;
    if (superClassName != null) superClassName.setParent(this);
  }

  public Expression getSuperClassName() {
    return superClassName;
  }

  public void setSuperClassName(Expression superClassName) {
    this.superClassName = superClassName;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (superClassName != null) superClassName.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (superClassName != null) superClassName.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (superClassName != null) superClassName.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("SuperExpression(\n");
      if (superClassName != null)
        buffer.append(superClassName.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [SuperExpression]");
    return buffer.toString();
  }
}
