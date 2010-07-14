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

public class Dims extends SyntaxNode {

  private SyntaxNode parent;
  private int numDimension;

  public Dims (int numDimension) {
    this.numDimension = numDimension;
  }

  public int getNumDimension() {
    return numDimension;
  }

  public void setNumDimension(int numDimension) {
    this.numDimension = numDimension;
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
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("Dims(\n");
    buffer.append("  "+tab+numDimension);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [Dims]");
    return buffer.toString();
  }
}
