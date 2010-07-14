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

public class Literal extends SyntaxNode {

  private SyntaxNode parent;
  private com.sc2mod.andromeda.vm.data.DataObject value;
  private int type;

  public Literal (com.sc2mod.andromeda.vm.data.DataObject value, int type) {
    this.value = value;
    this.type = type;
  }

  public com.sc2mod.andromeda.vm.data.DataObject getValue() {
    return value;
  }

  public void setValue(com.sc2mod.andromeda.vm.data.DataObject value) {
    this.value = value;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
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
    buffer.append("Literal(\n");
    buffer.append("  "+tab+value);
    buffer.append("\n");
    buffer.append("  "+tab+type);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [Literal]");
    return buffer.toString();
  }
}
