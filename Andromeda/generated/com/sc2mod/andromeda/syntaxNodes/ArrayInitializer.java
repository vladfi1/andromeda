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

public class ArrayInitializer extends SyntaxNode {

  private SyntaxNode parent;
  private VariableInitializers inits;
  private boolean hasComma;

  public ArrayInitializer (VariableInitializers inits, boolean hasComma) {
    this.inits = inits;
    if (inits != null) inits.setParent(this);
    this.hasComma = hasComma;
  }

  public VariableInitializers getInits() {
    return inits;
  }

  public void setInits(VariableInitializers inits) {
    this.inits = inits;
  }

  public boolean getHasComma() {
    return hasComma;
  }

  public void setHasComma(boolean hasComma) {
    this.hasComma = hasComma;
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (inits != null) inits.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (inits != null) inits.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (inits != null) inits.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ArrayInitializer(\n");
      if (inits != null)
        buffer.append(inits.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+hasComma);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ArrayInitializer]");
    return buffer.toString();
  }
}
