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

public class LiteralExpression extends Expression {

  private Literal literal;

  public LiteralExpression (Literal literal) {
    this.literal = literal;
    if (literal != null) literal.setParent(this);
  }

  public Literal getLiteral() {
    return literal;
  }

  public void setLiteral(Literal literal) {
    this.literal = literal;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (literal != null) literal.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (literal != null) literal.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (literal != null) literal.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("LiteralExpression(\n");
      if (literal != null)
        buffer.append(literal.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [LiteralExpression]");
    return buffer.toString();
  }
}
